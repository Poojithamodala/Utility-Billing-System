package com.utility.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.ConsumerApprovedEvent;
import com.utility.dto.ConsumerRejectedEvent;
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
    
    @Override
    public void sendApprovalEmail(ConsumerApprovedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject("Your Account Has Been Approved.");
        message.setText(
            "Hello " + event.getName() + ",\n\n" +
            "Your account has been approved by the admin.\n" +
            "You can now activate your account and log in.\n\n" +
            "Regards,\nUtility Billing Team"
        );

        mailSender.send(message);
    }
    
    @Override
    public void sendRejectionEmail(ConsumerRejectedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject("Account Registration Update");
        message.setText(
            "Hello " + event.getName() + ",\n\n" +
            "Unfortunately, your registration request was rejected.\n" +
            "Reason: " + event.getReason() + "\n\n" +
            "You may contact support for more information.\n\n" +
            "Regards,\nUtility Billing Team"
        );

        mailSender.send(message);
    }

}