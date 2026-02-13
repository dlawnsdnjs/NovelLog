package com.example.novelcharacter.domain.Episode.dto;

import com.example.novelcharacter.domain.Episode.entity.Episode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpisodeDTO {
    private long episodeNum;
    private long novelNum;
    private String episodeTitle;
    private String episodeSummary;
    private long orderIndex;

    public static EpisodeDTO from(Episode episode) {
        return EpisodeDTO.builder()
                .episodeNum(episode.getEpisodeNum())
                .novelNum(episode.getNovel().getNovelNum())
                .episodeTitle(episode.getEpisodeTitle())
                .episodeSummary(episode.getEpisodeSummary())
                .orderIndex(episode.getOrderIndex())
                .build();
    }
}
