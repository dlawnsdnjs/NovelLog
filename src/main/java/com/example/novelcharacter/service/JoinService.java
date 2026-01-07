package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.User.JoinDTO;
import com.example.novelcharacter.dto.User.UserDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * {@code JoinService} 클래스는 사용자의 회원가입 및 이메일 인증 과정을 관리하는 서비스입니다.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>회원가입 처리 및 중복 검사</li>
 *     <li>이메일 인증번호 생성 및 발송</li>
 *     <li>인증번호 검증 및 Redis 연동</li>
 * </ul>
 *
 * <p>이 클래스는 {@link UserService}, {@link EmailServiceImpl}, {@link RedisService} 등과 협력하여
 * 회원가입 절차 전반을 수행합니다.</p>
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JoinService {

    /** 사용자 관련 기능을 수행하는 서비스 */
    private final UserService userService;

    /** 비밀번호 암호화를 위한 BCrypt 인코더 */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /** 이메일 인증 코드 Redis 저장 시 사용하는 키 접두사 */
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    /** 이메일 발송을 담당하는 서비스 */
    private final EmailService mailService;

    /** Redis를 통한 인증 코드 관리 서비스 */
    private final RedisService redisService;

    /** 인증 코드 유효 시간 (밀리초 단위) */
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    /**
     * 회원가입 절차를 처리합니다.
     * <p>
     * 다음 절차로 수행됩니다:
     * <ol>
     *     <li>아이디 및 이메일 중복 검사</li>
     *     <li>Redis에서 이메일 인증 완료 여부 확인</li>
     *     <li>인증 완료 시, 비밀번호를 암호화하여 DB에 사용자 정보 저장</li>
     *     <li>UUID를 기반으로 랜덤 닉네임을 생성하며, UNIQUE 제약 위반 시 재시도</li>
     * </ol>
     *
     * @param joinDTO 회원가입 요청 정보를 담은 DTO (아이디, 비밀번호, 이메일, 인증코드 포함)
     * @throws RuntimeException 아이디 또는 이메일이 중복된 경우
     */
    public void joinProcess(JoinDTO joinDTO) {
        String userId = joinDTO.getId();
        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();
        String joinToken = joinDTO.getJoinToken();

        // 아이디 및 이메일 중복 검사
        this.checkDuplicatedId(userId);
        this.checkDuplicatedEmail(email);

        // Redis에서 인증 완료 여부 확인
        String authComplete = redisService.getValues("JoinToken:"+joinToken);

        // 인증 완료 시 회원가입 처리
        if (email.equals(authComplete)) {
            UserDTO data = new UserDTO();
            data.setUserId(userId);
            data.setPassword(bCryptPasswordEncoder.encode(password));
            data.setEmail(email);
            data.setRole("ROLE_USER");
            data.setLastLoginDate(LocalDate.now());
            data.setPrivacy_agreed_at(LocalDate.now());

            boolean saved = false;
            while (!saved) {
                try {
                    String randomNickname = "User_" + UUID.randomUUID().toString().substring(0, 6);
                    data.setUserName(randomNickname);
                    userService.insertUser(data);
                    saved = true;
                    redisService.deleteValues("JoinToken:"+joinToken);
                } catch (DuplicateKeyException e) {
                    // username UNIQUE 제약 위반 시 닉네임 재생성 후 재시도
                }
            }

        }
    }

    /**
     * 아이디 중복 여부를 확인합니다.
     *
     * @param userId 검사할 사용자 아이디
     * @throws RuntimeException 아이디가 이미 존재하는 경우
     */
    public void checkDuplicatedId(String userId) {
        boolean user = userService.isExistByUserId(userId);
        if (user) {
            log.debug("JoinService.checkDuplicatedId exception occur email: {}", userId);
            throw new RuntimeException("JoinService.checkDuplicatedId exception occur email: " + userId);
        }
    }

    /**
     * 이메일로 인증 코드를 발송합니다.
     * <p>
     * 인증 요청 시 이메일 중복을 먼저 검사하고,
     * 6자리 인증 코드를 생성하여 메일로 전송한 뒤 Redis에 저장합니다.
     *
     * @param toEmail 수신자 이메일 주소
     * @throws MessagingException 이메일 전송 실패 시
     * @throws RuntimeException 이메일이 이미 등록된 경우
     */
    public String sendCodeToEmail(String toEmail) throws MessagingException {
        this.checkDuplicatedEmail(toEmail);

        String title = "Novel Log 이메일 인증 번호";
        String authCode = this.createCode();

        mailService.sendEmail(toEmail, title, authCode);

        // Redis에 인증 코드 저장 (key = "AuthCode " + email, value = authCode)
        redisService.setValues(
                AUTH_CODE_PREFIX + toEmail,
                authCode,
                Duration.ofMillis(this.authCodeExpirationMillis)
        );

        return authCode;
    }

    /**
     * 이메일 중복 여부를 확인합니다.
     *
     * @param email 검사할 이메일 주소
     * @throws RuntimeException 이메일이 이미 존재하는 경우
     */
    private void checkDuplicatedEmail(String email) {
        boolean user = userService.isExistByEmail(email);
        if (user) {
            log.debug("JoinService.checkDuplicatedEmail exception occur email: {}", email);
            throw new RuntimeException("JoinService.checkDuplicatedEmail exception occur email: " + email);
        }
    }

    /**
     * 6자리 난수 형태의 인증 코드를 생성합니다.
     *
     * @return 생성된 6자리 인증 코드
     * @throws RuntimeException 보안 알고리즘이 지원되지 않는 경우
     */
    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("JoinService.createCode() exception occur");
            throw new RuntimeException("JoinService.createCode() exception occur");
        }
    }

    /**
     * 이메일 인증 코드를 검증합니다.
     * <p>
     * Redis에 저장된 인증 코드와 사용자가 입력한 코드를 비교하여
     * 일치하는 경우 인증 성공으로 간주하고, 추가 인증 완료 키를 Redis에 저장합니다.
     *
     * @param email    사용자 이메일 주소
     * @param authCode 사용자가 입력한 인증 코드
     * @return 인증 성공 시 {@code true}, 실패 시 {@code false}
     * @throws RuntimeException 이메일이 이미 존재하는 경우
     */
    public String verifiedCode(String email, String authCode) {
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);

        if (redisAuthCode != null && redisAuthCode.equals(authCode)) {
            redisService.deleteValues(AUTH_CODE_PREFIX + email);

            String joinToken = UUID.randomUUID().toString();
            // 인증 완료 상태 저장
            redisService.setValues( "JoinToken:"+joinToken, email, Duration.ofMillis(authCodeExpirationMillis));
            return joinToken;
        }

        throw new RuntimeException("인증코드 불일치");
    }
}
