package com.example.novelcharacter.service;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Profile("dev")
public class EmailServiceTest implements EmailService {

    @Override
    public void sendEmail(String to, String title, String content) {
        // [최적의 구현] 아무 작업도 하지 않고 즉시 리턴합니다.
        // 부하 테스트 환경에서는 로깅마저도 CPU/Disk I/O 부하가 될 수 있으므로,
        // 완전히 비우는 것이 가장 고성능입니다.
    }

    @Override
    public SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        return null;
    }
}
