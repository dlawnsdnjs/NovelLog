package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.User.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final RedisService redisService;
    private final UserService userService;
    private final MailTemplateService templateService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder;

    @Value("${BACK_URL}")
    private String BACK_URL;


    public boolean sendResetEmail(String userId) throws MessagingException {
        // 등록된 이메일인지 확인
        UserDTO user = userService.getUserById(userId);
        if (user == null) {
            return false;  // 존재하지 않음
        }

        // 토큰 생성
        String token = UUID.randomUUID().toString();
        redisService.setValues("PW_RESET_" + token, user.getUserId(), Duration.ofMinutes(15));

        // reset url (프론트 url)
        String resetLink = BACK_URL + "/page/reset-password?token=" + token;

        String html;
        try {
            html = templateService.loadTemplate(
                    "reset-password.html",
                    Map.of("resetLink", resetLink)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        emailService.sendEmail(user.getEmail(), "비밀번호 재설정 안내", html);

        return true; // 메일 발송 성공
    }

    public void resetPassword(String token, String newPassword) {
        String userId = redisService.getValues("PW_RESET_" + token);
        if (userId == null) throw new RuntimeException("토큰이 만료되었거나 유효하지 않습니다");

        // 비밀번호 변경
        userService.updatePassword(userId, encoder.encode(newPassword));

        // token 1회성 사용: 삭제
        redisService.deleteValues("PW_RESET_" + token);
    }

    public void changePassword(long uuid, String currentPassword, String newPassword) {
        UserDTO userDTO = userService.getUserByUuid(uuid);
        if(userDTO.getPassword().equals(encoder.encode(currentPassword))) {
            userService.updatePassword(userDTO.getUserId(), encoder.encode(newPassword));
        }
    }
}
