package com.example.gbsports.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        try {
            // In cấu hình để debug (không cần hardcode)
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
            logger.info("Mail configuration - Host: {}, Port: {}, Username: {}, Password: {}",
                    mailSenderImpl.getHost(),
                    mailSenderImpl.getPort(),
                    mailSenderImpl.getUsername(),
                    mailSenderImpl.getPassword());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new MessagingException("Failed to send email: " + e.getMessage(), e);
        }
    }
}