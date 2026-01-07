package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.FavoriteDTO;
import com.example.novelcharacter.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * {@code FavoriteService} 클래스는 사용자의 즐겨찾기(Favorite) 기능을 관리하는 서비스입니다.
 * <p>
 * 이 클래스는 사용자가 특정 항목(예: 소설, 회차 등)을 즐겨찾기에 추가하거나,
 * 이미 즐겨찾기된 경우 해당 즐겨찾기를 해제하는 기능을 제공합니다.
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>즐겨찾기 등록 또는 해제</li>
 *   <li>즐겨찾기 삭제</li>
 * </ul>
 *
 * <p>이 서비스는 {@link FavoriteMapper}를 통해 데이터베이스와 연동됩니다.</p>
 */
@RequiredArgsConstructor
@Service
public class FavoriteService {

    /** 즐겨찾기 관련 DB 작업을 수행하는 매퍼 */
    private final FavoriteMapper favoriteMapper;

    /**
     * 즐겨찾기를 설정하거나 해제합니다.
     * <p>
     * 해당 즐겨찾기가 존재하지 않으면 추가하고,
     * 이미 존재한다면 {@link #deleteFavorite(FavoriteDTO)}를 호출하여 제거합니다.
     *
     * @param favoriteDTO 즐겨찾기 정보가 담긴 DTO
     */
    public void setFavorite(FavoriteDTO favoriteDTO) {
        if (favoriteMapper.getFavorite(favoriteDTO) == null) {
            favoriteMapper.addFavorite(favoriteDTO);
        } else {
            deleteFavorite(favoriteDTO);
        }
    }

    /**
     * 즐겨찾기를 삭제합니다.
     * <p>
     * DB에서 해당 즐겨찾기 정보를 제거합니다.
     *
     * @param favoriteDTO 삭제할 즐겨찾기 정보를 담은 DTO
     */
    public void deleteFavorite(FavoriteDTO favoriteDTO) {
        favoriteMapper.removeFavorite(favoriteDTO);
    }
}
