package com.example.novelcharacter.dto.Novel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NovelDTO {
    private long novelNum;
    @NotBlank
    @Size(max=40)
    private String novelTitle;
    private long uuid;
}
