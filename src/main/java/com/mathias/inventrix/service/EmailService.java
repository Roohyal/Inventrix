package com.mathias.inventrix.service;

import com.mathias.inventrix.payload.request.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails, String templateName) throws MessagingException;
}
