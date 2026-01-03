package com.utility.service;

import com.utility.dto.BillGeneratedEvent;
import com.utility.dto.PaymentSuccessEvent;

public interface EmailService {
    void sendBillGeneratedEmail(BillGeneratedEvent event);
    void sendPaymentSuccessEmail(PaymentSuccessEvent event);
}

