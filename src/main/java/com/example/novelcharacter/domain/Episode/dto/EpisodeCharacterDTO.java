package com.example.novelcharacter.domain.Episode.dto;

import com.example.novelcharacter.domain.Episode.entity.Episode;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EpisodeCharacterDTO {
    private long episodeNum;
    private long characterNum;

    public static EpisodeCharacterDTO from(EpisodeCharacter episodeCharacter) {
        return EpisodeCharacterDTO.builder()
                .episodeNum(episodeCharacter.getEpisode().getEpisodeNum())
                .characterNum(episodeCharacter.getCharacter().getCharacterNum())
                .build();
    }
}
