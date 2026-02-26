package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.Favorite;
import com.example.novelcharacter.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
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
 */
@RequiredArgsConstructor
@Service
public class FavoriteService {

    /** 즐겨찾기 관련 DB 작업을 수행하는 매퍼 */
    private final FavoriteRepository favoriteRepository;

    /**
     * 즐겨찾기를 설정하거나 해제합니다.
     * <p>
     * 해당 즐겨찾기가 존재하지 않으면 추가하고,
     * 이미 존재한다면 {@link #deleteFavorite(Favorite)}를 호출하여 제거합니다.
     *
     * @param favorite 즐겨찾기 정보가 담긴 DTO
     */
    public void setFavorite(Favorite favorite) {
        if (favoriteRepository.findFavoriteById(favorite.getId()) == null) {
            favoriteRepository.save(favorite);
        } else {
            deleteFavorite(favorite);
        }
    }

    /**
     * 즐겨찾기를 삭제합니다.
     * <p>
     * DB에서 해당 즐겨찾기 정보를 제거합니다.
     *
     * @param favorite 삭제할 즐겨찾기 정보를 담은 DTO
     */
    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }
}
