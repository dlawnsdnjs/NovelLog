package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Episode.EpisodeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EpisodeMapper {
    public void insertEpisode(EpisodeDTO episodeDTO);
    public void updateOrderIndexNull(EpisodeDTO episodeDTO);
    public List<EpisodeDTO> selectAllEpisode(long novelNum);
    public List<EpisodeDTO> selectEpisodePage(long novelNum, int offset);
//    public void rebalanceOrderIndex(long novelNum);
    public EpisodeDTO selectEpisodeById(long episodeNum);
    public int checkEpisodeOwner(long episodeNum, long uuid);
    public List<EpisodeDTO> searchEpisode(String search);
    public void updateEpisode(EpisodeDTO episodeDTO);
    public void deleteEpisode(long episodeNum);
}
