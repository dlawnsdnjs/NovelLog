package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Board.entity.BoardCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardCategoryMapper {
    public List<BoardCategory> selectAllBoardCategory();
}
