package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Board.BoardCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardCategoryMapper {
    public List<BoardCategoryDTO> selectAllBoardCategory();
}
