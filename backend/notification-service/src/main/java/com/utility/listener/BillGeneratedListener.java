package com.utility.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.ConsumerApprovedEvent;
import com.utility.dto.ConsumerRejectedEvent;
import com.utility.dto.PaymentSuccessEvent;
import com.utility.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillGeneratedListener {

	private final EmailService emailService;

	@KafkaListener(topics = "bill-generated-topic", groupId = "notification-group")
	public void handleBillGenerated(BillGeneratedEvent event) {
		log.info("Received BillGeneratedEvent: {}", event);
		try {
			emailService.sendBillGeneratedEmail(event);
			log.info("Bill generated email sent to {}", event.getConsumerEmail());
		} catch (Exception e) {
			log.error("Failed to send bill generated email", e);
		}
	}
	
	@KafkaListener(topics = "payment-success-topic", groupId = "notification-group")
	public void handlePaymentSuccess(PaymentSuccessEvent event) {
	    log.info("Received PaymentSuccessEvent: {}", event);
	    try {
	        emailService.sendPaymentSuccessEmail(event);
	        log.info("Payment success email sent to {}", event.getConsumerEmail());
	    } catch (Exception e) {
	        log.error("Failed to send payment success email", e);
	    }
	}
	
	@KafkaListener(
		    topics = "consumer-approved-topic",
		    groupId = "notification-group"
		)
		public void handleConsumerApproved(ConsumerApprovedEvent event) {
		    emailService.sendApprovalEmail(event);
		}
	
	@KafkaListener(
		    topics = "consumer-rejected-topic",
		    groupId = "notification-group"
		)
		public void handleConsumerRejected(ConsumerRejectedEvent event) {
		    emailService.sendRejectionEmail(event);
		}
}