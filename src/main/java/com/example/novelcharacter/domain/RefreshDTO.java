package com.example.novelcharacter.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RefreshDTO {
    private long id;
    private long uuid;
    private String refresh;
    private String expiration;
}
