package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Board.entity.BoardCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
    @NotNull
    public List<BoardCategory> findAll();
}
