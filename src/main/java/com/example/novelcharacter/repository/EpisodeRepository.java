package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Episode.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    public void updateOrderIndexNull(Episode episode);
    public List<Episode> findAllEpisodesByNovel_NovelNum(long novelNum);
    public List<Episode> findEpisodePage(long novelNum, int page);
    public Episode findEpisodeByEpisodeNum(long episodeNum);

    @Query("select count(e) > 0 " +
            "from Episode e join e.novel n " +
            "where e.episodeNum = :episodeNum and n.user.uuid = :uuid")
    public boolean existsByEpisodeNumAndUuid(@Param("episodeNum") long episodeNum, @Param("uuid") long uuid);
    public List<Episode> findByEpisodeTitleContaining(String search);
}
