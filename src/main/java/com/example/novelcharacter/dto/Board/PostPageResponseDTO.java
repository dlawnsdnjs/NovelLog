package com.example.novelcharacter.dto.Board;

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
