package com.example.novelcharacter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * 이메일 발송 기능을 담당하는 서비스 클래스입니다.
 *
 * <p>JavaMailSender를 이용해 일반 텍스트 메일과 HTML 포맷 메일을 전송할 수 있습니다.
 * 또한, 메일 발송 과정에서 발생할 수 있는 예외를 처리하여 안정적인 발송 기능을 제공합니다.</p>
 *
 * @author
 * @since 2025-10-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile({"!dev", "default"})
public class EmailServiceImpl implements EmailService {

    /** Spring에서 제공하는 메일 전송 객체 */
    private final JavaMailSender emailSender;

    /**
     * HTML 형식의 이메일을 발송합니다.
     *
     * <p>이 메서드는 {@link MimeMessageHelper}를 사용하여 수신자, 제목, 본문 등을 설정하고,
     * {@link JavaMailSender#send(MimeMessage)}를 통해 메일을 실제로 전송합니다.</p>
     *
     * @param toEmail 수신자 이메일 주소
     * @param title   메일 제목
     * @param content 메일 본문 (HTML 형식 가능)
     * @throws javax.mail.MessagingException MIME 메시지 생성 과정에서 오류가 발생한 경우
     * @throws RuntimeException 메일 전송에 실패한 경우 (예: SMTP 연결 실패, 잘못된 주소 등)
     */
    public void sendEmail(String toEmail, String title, String content) throws javax.mail.MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail); // 메일 수신자
        helper.setSubject(title); // 메일 제목
        helper.setText(content, true); // 본문 내용 (HTML 허용)
        helper.setReplyTo("no-reply@domain.com"); // 회신 불가 주소 설정
        helper.setFrom("no-reply@domain.com");

        try {
            emailSender.send(message);
        } catch (RuntimeException e) {
            log.error("이메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to send email in sendEmail", e);
        }
    }

    /**
     * 일반 텍스트 이메일 메시지 객체를 생성합니다.
     *
     * <p>이 메서드는 단순한 {@link SimpleMailMessage} 인스턴스를 생성하여
     * 수신자, 제목, 본문을 설정한 후 반환합니다.</p>
     *
     * @param toEmail 수신자 이메일 주소
     * @param title   메일 제목
     * @param text    메일 본문 내용 (순수 텍스트)
     * @return 설정된 {@link SimpleMailMessage} 객체
     */
    public SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        return message;
    }

    
}
