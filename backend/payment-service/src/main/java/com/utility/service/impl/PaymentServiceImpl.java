package com.utility.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.utility.config.BillingClient;
import com.utility.dto.BillStatus;
import com.utility.dto.MonthlyRevenueResponse;
import com.utility.dto.PaymentRequest;
import com.utility.dto.PaymentResponse;
import com.utility.dto.PaymentSuccessEvent;
import com.utility.model.Payment;
import com.utility.model.PaymentStatus;
import com.utility.repository.PaymentRepository;
import com.utility.service.PaymentService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository repository;
	private final BillingClient billingClient;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Override
	public Mono<PaymentResponse> makePayment(PaymentRequest request, String authHeader) {

	    if (request.getAmount() <= 0) {
	        return Mono.error(new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Payment amount must be greater than zero"
	        ));
	    }

	    return billingClient.getBill(request.getBillId(), authHeader)
	        .flatMap(bill -> {

	            if (bill.getStatus() == BillStatus.PAID) {
	                return Mono.error(new ResponseStatusException(
	                        HttpStatus.CONFLICT,
	                        "Bill is already fully paid"
	                ));
	            }

	            double outstanding = bill.getOutstandingAmount();

	            if (request.getAmount() > outstanding) {
	                return Mono.error(new ResponseStatusException(
	                        HttpStatus.BAD_REQUEST,
	                        "Payment exceeds outstanding amount"
	                ));
	            }

	            Payment payment = Payment.builder()
	                    .billId(bill.getId())
	                    .consumerId(bill.getConsumerId())
	                    .consumerEmail(bill.getConsumerEmail())
	                    .utilityType(bill.getUtilityType())
	                    .amountPaid(request.getAmount())
	                    .paymentMode(request.getPaymentMode())
	                    .paymentStatus(PaymentStatus.SUCCESS)
	                    .paymentDate(LocalDate.now())
	                    .referenceNumber("TXN-" + UUID.randomUUID())
	                    .build();

	            return repository.save(payment)
	                .flatMap(savedPayment -> {

	                    double newOutstanding = outstanding - request.getAmount();
	                    BillStatus newStatus =
	                            newOutstanding == 0 ? BillStatus.PAID : BillStatus.DUE;

	                    return billingClient.updateBillOutstanding(
	                            bill.getId(),
	                            newOutstanding,
	                            newStatus,
	                            authHeader
	                    )
	                    .then(Mono.fromRunnable(() ->
	                        kafkaTemplate.send(
	                            "payment-success-topic",
	                            new PaymentSuccessEvent(
	                                bill.getId(),
	                                bill.getConsumerEmail(),
	                                savedPayment.getAmountPaid(),
	                                savedPayment.getPaymentDate()
	                            )
	                        )
	                    ))
	                    .thenReturn(
	                        PaymentResponse.builder()
	                            .paymentId(savedPayment.getId())
	                            .consumerEmail(savedPayment.getConsumerEmail())
	                            .billId(savedPayment.getBillId())
	                            .utilityType(savedPayment.getUtilityType())
	                            .amountPaid(savedPayment.getAmountPaid())
	                            .remainingBalance(newOutstanding)
	                            .paymentMode(savedPayment.getPaymentMode())
	                            .paymentStatus(savedPayment.getPaymentStatus())
	                            .paymentDate(savedPayment.getPaymentDate())
	                            .referenceNumber(savedPayment.getReferenceNumber())
	                            .build()
	                    );
	                });
	        });
	}
	
	@Override
	public Flux<PaymentResponse> getAllPayments() {
	    return repository.findAll()
	            .map(this::toResponse);
	}

	@Override
	public Flux<PaymentResponse> getPaymentsForBill(String billId) {
		return repository.findByBillId(billId).map(this::toResponse);
	}

	@Override
	public Flux<PaymentResponse> getPaymentsForConsumer(String consumerId) {
		return repository.findByConsumerId(consumerId).map(this::toResponse);
	}
	
	@Override
    public Flux<MonthlyRevenueResponse> getMonthlyRevenue() {

        return repository.findAll()

            //only SUCCESS payments
            .filter(payment -> payment.getPaymentStatus() == PaymentStatus.SUCCESS)

            //map to (YearMonth, amount)
            .map(payment -> {
                String month = payment.getPaymentDate().getYear()
                        + "-" +
                        String.format("%02d", payment.getPaymentDate().getMonthValue());

                return Map.entry(month, payment.getAmountPaid());
            })

            //group by month
            .groupBy(Map.Entry::getKey)

            //sum per month
            .flatMap(group ->
                group.map(Map.Entry::getValue)
                     .reduce(0.0, Double::sum)
                     .map(total ->
                         new MonthlyRevenueResponse(group.key(), total)
                     )
            )

            //sort by month
            .sort(Comparator.comparing(MonthlyRevenueResponse::getMonth));
    }

	private PaymentResponse toResponse(Payment payment) {
		return PaymentResponse.builder()
				.paymentId(payment.getId())
				.consumerEmail(payment.getConsumerEmail())
				.billId(payment.getBillId())
				.utilityType(payment.getUtilityType())
				.amountPaid(payment.getAmountPaid())
				.remainingBalance(0)
				.paymentMode(payment.getPaymentMode())
				.paymentStatus(payment.getPaymentStatus())
				.paymentDate(payment.getPaymentDate())
				.referenceNumber(payment.getReferenceNumber())
				.build();
	}
}