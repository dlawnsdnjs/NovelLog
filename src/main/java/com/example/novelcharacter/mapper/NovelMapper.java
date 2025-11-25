package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Novel.NovelDTO;
import com.example.novelcharacter.dto.Novel.NovelWithFavoriteDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NovelMapper {
    public void insertNovel(NovelDTO novelDTO);
    public List<NovelWithFavoriteDTO> selectAllNovel(long uuid);
    public NovelDTO selectNovelById(long novelNum);
    public int checkOwner(long novelNum, long uuid);
    public List<NovelDTO> searchNovel(String search);
    public void updateNovel(NovelDTO novelDTO);
    public void deleteNovel(long novelNum);
}
