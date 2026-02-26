package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Novel.dto.NovelWithFavoriteDTO;
import com.example.novelcharacter.domain.Novel.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NovelRepository extends JpaRepository <Novel, Long> {
    @Query("select new com.example.novelcharacter.domain.Novel.dto.NovelWithFavoriteDTO(n.novelNum, n.novelTitle, u.uuid, case when f.id is null then false else true end) " +
            "from Novel n join n.user u " +
            "left join Favorite f on f.id.targetId = n.novelNum " +
            "and f.id.targetType = 'Novel' " +
            "and f.user.uuid = :uuid " +
            "where n.user.uuid = :uuid " +
            "order by case when f.id.targetId is null then 0 else 1 end desc")
    public List<NovelWithFavoriteDTO> findAllByUuid(@Param("uuid") long uuid);
    public Novel findNovelByNovelNum(long novelNum);
    public List<Novel> findByNovelTitleContainingIgnoreCase(String search);
    public boolean existsByNovelNumAndUser_Uuid(long novelNum, long uuid);
}
