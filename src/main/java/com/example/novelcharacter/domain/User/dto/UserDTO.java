package com.example.novelcharacter.domain.User.dto;

import com.example.novelcharacter.domain.User.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long uuid;
    private String userId;
    private String userName;
    private String email;
    private String password;
    private String role;
    private LocalDate lastLoginDate;
    private LocalDate privacy_agreed_at;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .uuid(user.getUuid())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .lastLoginDate(user.getLastLoginDate())
                .privacy_agreed_at(user.getPrivacy_agreed_at())
                .build();
    }
}
