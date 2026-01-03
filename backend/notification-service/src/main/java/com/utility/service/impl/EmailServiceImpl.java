package com.utility.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.PaymentSuccessEvent;
import com.utility.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendBillGeneratedEmail(BillGeneratedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getConsumerEmail());
        message.setSubject("Your Utility Bill Has Been Generated");

        message.setText(
            "Hello,\n\n" +
            "Your bill has been generated successfully.\n\n" +
            "Bill ID: " + event.getBillId() + "\n" +
            "Amount: ₹" + event.getAmount() + "\n" +
            "Due Date: " + event.getDueDate() + "\n\n" +
            "Please make the payment before due date.\n\n" +
            "Thank you."
        );

        mailSender.send(message);
    }
    
    @Override
    public void sendPaymentSuccessEmail(PaymentSuccessEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getConsumerEmail());
        message.setSubject("Payment Successful");
        message.setText(
            "Your payment of ₹" + event.getAmountPaid() +
            " has been successfully received on " + event.getPaymentDate() +
            ".\n\nThank you for your payment."
        );

        mailSender.send(message);
        log.info("Payment success email sent to {}", event.getConsumerEmail());
    }

}