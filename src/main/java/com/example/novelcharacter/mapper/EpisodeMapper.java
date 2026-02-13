package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Episode.entity.Episode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EpisodeMapper {
    public void insertEpisode(Episode episode);
    public void updateOrderIndexNull(Episode episode);
    public List<Episode> selectAllEpisode(long novelNum);
    public List<Episode> selectEpisodePage(long novelNum, int offset);
//    public void rebalanceOrderIndex(long novelNum);
    public Episode selectEpisodeById(long episodeNum);
    public int checkEpisodeOwner(long episodeNum, long uuid);
    public List<Episode> searchEpisode(String search);
    public void updateEpisode(Episode episode);
    public void deleteEpisode(long episodeNum);
}
