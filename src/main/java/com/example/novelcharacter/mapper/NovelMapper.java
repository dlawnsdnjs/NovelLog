package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Novel.entity.Novel;
import com.example.novelcharacter.domain.Novel.dto.NovelWithFavoriteDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NovelMapper {
    public void insertNovel(Novel novel);
    public List<NovelWithFavoriteDTO> selectAllNovel(long uuid);
    public Novel selectNovelById(long novelNum);
    public int checkOwner(long novelNum, long uuid);
    public List<Novel> searchNovel(String search);
    public void updateNovel(Novel novel);
    public void deleteNovel(long novelNum);
}
