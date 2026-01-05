package com.example.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendLoginSuccessMail(String toEmail) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Login Successful");
        message.setText(
            "Hi,\n\n" +
            "Your account was logged in successfully.\n\n" +
            "If this wasn't you, please reset your password immediately.\n\n" +
            "Regards,\nSupport Team"
        );

        mailSender.send(message);
    }
}
