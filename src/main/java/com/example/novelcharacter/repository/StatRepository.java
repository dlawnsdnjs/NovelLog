package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Stat.entity.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Integer> {
    public List<Stat> findStatsByStatNameIn(List<String> statNames);
    public Stat findStatByStatCode(long statCode);
    public Stat findStatByStatName(String statName);
}
