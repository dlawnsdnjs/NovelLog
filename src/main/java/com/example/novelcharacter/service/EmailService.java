package com.example.novelcharacter.service;

import jakarta.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    public void sendEmail(String toEmail, String title, String content) throws MessagingException;
    public SimpleMailMessage createEmailForm(String toEmail, String title, String text);
}
