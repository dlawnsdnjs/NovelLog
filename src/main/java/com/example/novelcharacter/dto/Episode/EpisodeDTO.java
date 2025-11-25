package com.example.novelcharacter.dto.Episode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EpisodeDTO {
    private long novelNum;
    private long episodeNum;
    @NotBlank
    @Size(max=40)
    private String episodeTitle;
    private String episodeSummary;
    private long orderIndex;
}
