package com.example.novelcharacter.domain.Stat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatRequestDTO {
    private long statCode;
    private long value;
}
