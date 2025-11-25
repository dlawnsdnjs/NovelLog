package com.example.novelcharacter.dto.Stat;

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
