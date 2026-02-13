package com.example.novelcharacter.repository;


import com.example.novelcharacter.domain.Favorite;
import com.example.novelcharacter.domain.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    public Favorite findFavoriteById(FavoriteId favoriteId);
}
