package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper {
    public void addFavorite(Favorite favorite);
    public void removeFavorite(Favorite favorite);
    public Favorite getFavorite(Favorite favorite);
}
