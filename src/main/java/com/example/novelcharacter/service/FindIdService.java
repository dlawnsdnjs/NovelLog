package com.example.novelcharacter.service;

import com.example.novelcharacter.component.MaskUtil;
import com.example.novelcharacter.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindIdService {

    private final UserService userService;
    private final MailTemplateService templateService;
    private final EmailService emailService;

    public boolean sendUserId(String email) throws MessagingException {

        // 1) 이메일 존재확인
        UserDTO user = userService.findByEmail(email);
        if (user == null) {
            return false;
        }
        // OAuthUser 의 경우 별도 처리
        if ("OAuthUser".equals(user.getPassword())) {
            String html;
            try {
                html = templateService.loadTemplate(
                        "find-userid-oauth.html",
                        Map.of("email", email)
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            emailService.sendEmail(email, "SNS 로그인 안내", html);
            return true;
        }
        String maskedId = MaskUtil.maskUserId(user.getUserId());

        String html;
        try {
            html = templateService.loadTemplate(
                    "find-userid-email.html",
                    Map.of("maskedUserId", maskedId)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        emailService.sendEmail(email, "요청하신 아이디 안내", html);

        return true; // 메일 발송 성공
    }
}
