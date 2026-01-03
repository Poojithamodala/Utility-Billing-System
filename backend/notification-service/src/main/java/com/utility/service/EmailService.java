package com.utility.service;

import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.ConsumerApprovedEvent;
import com.utility.dto.ConsumerRejectedEvent;
import com.utility.dto.PaymentSuccessEvent;

public interface EmailService {
    void sendBillGeneratedEmail(BillGeneratedEvent event);
    void sendPaymentSuccessEmail(PaymentSuccessEvent event);
    void sendApprovalEmail(ConsumerApprovedEvent event);
    void sendRejectionEmail(ConsumerRejectedEvent event);
}

