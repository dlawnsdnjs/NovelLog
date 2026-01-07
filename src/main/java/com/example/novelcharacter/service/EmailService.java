package com.example.novelcharacter.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    public void sendEmail(String toEmail, String title, String content) throws javax.mail.MessagingException ;
    public SimpleMailMessage createEmailForm(String toEmail, String title, String text);
}
