package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.service.FindIdService;
import com.example.novelcharacter.service.ResetPasswordService;
import com.example.novelcharacter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * 사용자 정보 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>JWT 토큰을 이용해 인증된 사용자의 정보를 변경하는 기능을 제공합니다.</p>
 * <p>현재는 사용자 이름(username) 변경 기능만 포함되어 있습니다.</p>
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FindIdService findIdService;
    private final ResetPasswordService resetPasswordService;
    private final JWTUtil jwtUtil;


    @GetMapping("/duplicateCheck")
    public ResponseEntity<String> duplicateCheck(@RequestParam String userName) {
        if(userService.checkDuplicateName(userName)){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("이미 사용 중인 아이디입니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }

    /**
     * 사용자 이름을 변경합니다.
     *
     * <p>클라이언트에서 전달된 Access Token을 검증하여 UUID를 추출하고,
     * 해당 사용자의 이름을 요청 본문에 포함된 새로운 이름으로 업데이트합니다.</p>
     *
     * @param access JWT Access Token (요청 헤더에서 전달)
     * @param body   새로운 사용자 이름을 포함한 요청 본문 (예: {"username": "newName"})
     * @throws Exception 이름이 중복되거나 변경 권한이 없을 경우 발생
     */
    @PatchMapping("/userUpdate")
    public void userUpdate(@RequestHeader("Access") String access, @RequestBody Map<String, String> body) throws DuplicateMemberException {
        long uuid = jwtUtil.getUuid(access);
        String userName = body.get("userName");
        userService.updateUserName(userName, uuid);
    }

    @PatchMapping("/userPasswordChange")
    public void userPasswordChange(@RequestHeader("Access") String access, @RequestBody Map<String, String> body) throws DuplicateMemberException {
        long uuid = jwtUtil.getUuid(access);
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        resetPasswordService.changePassword(uuid, currentPassword, newPassword);

    }

    @PostMapping("/userIdFind")
    public ResponseEntity<?> userIdFind(@RequestBody Map<String, String> body) throws DuplicateMemberException, MessagingException {
        String email = body.get("email");
        boolean sent = findIdService.sendUserId(email);
        if (!sent) {
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok().build(); // 200
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> sendReset(@RequestBody Map<String, String> request) throws DuplicateMemberException, MessagingException {
        String userId = request.get("userId");

        boolean sent = resetPasswordService.sendResetEmail(userId);
        if (!sent) {
            return ResponseEntity.noContent().build(); // 204: 존재하지 않는 이메일
        }

        return ResponseEntity.ok().build(); // 200: 메일 발송
    }

    @PostMapping("/resetPassword/confirm")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {

        String token = body.get("token");
        String newPassword = body.get("newPassword");

        try {
            resetPasswordService.resetPassword(token, newPassword);
            return ResponseEntity.ok().build(); // 성공
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Invalid token");
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Access") String access) {
        long uuid = jwtUtil.getUuid(access);
        try {
            userService.deleteUser(uuid);
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid token");
        }
        return ResponseEntity.ok().build();
    }

}
