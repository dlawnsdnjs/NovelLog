package com.example.novelcharacter.domain.Board.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostPageResponseDTO {
    private List<PostDataDTO> data;
    private long totalCount;
}
