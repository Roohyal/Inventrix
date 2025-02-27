package com.mathias.inventrix.service.impl;

import com.mathias.inventrix.payload.request.EmailDetails;
import com.mathias.inventrix.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("EMAIL_USER")
    private String senderEmail;


    @Override
    public void sendEmailAlert(EmailDetails emailDetails, String templateName) throws  MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        Map<String, Object> variables = Map.of(
                "name", emailDetails.getFullName(),
                "company", emailDetails.getCompanyId(),
                "link", emailDetails.getLink()

        );
        context.setVariables(variables);

        mimeMessageHelper.setFrom(senderEmail);
        mimeMessageHelper.setTo(emailDetails.getRecipient());
        mimeMessageHelper.setSubject(emailDetails.getSubject());

        String html = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("Sending email: to {}", emailDetails.getRecipient());
    }

    @Override
    public void sendEmployeeAlert(EmailDetails emailDetails, String templateName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        Map<String, Object> variables = Map.of(
                "name", emailDetails.getFullName(),
                "company", emailDetails.getCompanyName(),
                "password", emailDetails.getPassword(),
                "link", emailDetails.getLink()

        );
        context.setVariables(variables);

        mimeMessageHelper.setFrom(senderEmail);
        mimeMessageHelper.setTo(emailDetails.getRecipient());
        mimeMessageHelper.setSubject(emailDetails.getSubject());

        String html = templateEngine.process(templateName, context);
        mimeMessageHelper.setText(html, true);

        mailSender.send(mimeMessage);
        log.info("Sending email: to {}", emailDetails.getRecipient());

    }
}
