package com.example.novelcharacter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JoinDTO {
    @NotBlank
    @Pattern(regexp="^[a-zA-Z0-9]{4,20}$",
            message="아이디는 영문 숫자만 4~20자 가능합니다.")
    private String id;
    private String email;
    @NotBlank
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message="비밀번호는 문자/숫자/특수문자 포함 8~20자")
    private String password;
    private String authCode;
}
