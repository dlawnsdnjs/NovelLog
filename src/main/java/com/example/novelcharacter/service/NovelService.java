package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.Favorite;
import com.example.novelcharacter.domain.FavoriteId;
import com.example.novelcharacter.domain.Novel.dto.NovelDTO;
import com.example.novelcharacter.domain.Novel.entity.Novel;
import com.example.novelcharacter.domain.Novel.dto.NovelWithFavoriteDTO;
import com.example.novelcharacter.domain.User.entity.User;
import com.example.novelcharacter.repository.NovelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;

/**
 * {@code NovelService} 클래스는 소설(Novel) 데이터의 생성, 조회, 수정, 삭제 및 즐겨찾기(Favorite) 기능을 제공하는 서비스입니다.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>소설 등록 및 조회</li>
 *     <li>작성자 권한(소유자) 확인</li>
 *     <li>즐겨찾기 토글 기능</li>
 * </ul>
 *
 * {@link FavoriteService}를 통해 즐겨찾기 상태를 관리합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NovelService {
    /** 소설 관련 데이터베이스 작업을 담당하는 Mapper */
    private final NovelRepository novelRepository;

    /** 즐겨찾기 상태를 관리하는 서비스 */
    private final FavoriteService favoriteService;
    private final UserService userService;

    /**
     * 새로운 소설을 생성하고 데이터베이스에 저장합니다.
     * <p>
     * 소설 제목이 비어 있는 경우 {@link IllegalArgumentException}이 발생합니다.
     * 저장 후, 기본적으로 즐겨찾기 상태는 {@code false}로 설정됩니다.
     *
     * @param novelTitle 생성할 소설의 제목
     * @param uuid       소설 작성자의 UUID
     * @return 생성된 소설 정보를 포함한 {@link NovelWithFavoriteDTO}
     * @throws IllegalArgumentException 소설 제목이 null이거나 비어 있는 경우
     */
    public NovelWithFavoriteDTO insertNovel(String novelTitle, long uuid) {
        if (novelTitle == null || novelTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Novel Title cannot be null or empty");
        }

        Novel newNovel = new Novel();
        newNovel.setNovelTitle(novelTitle);
        User user = userService.getUserProxy(uuid);

        newNovel.setUser(user);
        novelRepository.save(newNovel);

        NovelWithFavoriteDTO novelWithFavoriteDTO = new NovelWithFavoriteDTO();
        novelWithFavoriteDTO.setNovelTitle(novelTitle);
        novelWithFavoriteDTO.setUuid(uuid);
        novelWithFavoriteDTO.setNovelNum(newNovel.getNovelNum());
        novelWithFavoriteDTO.setFavorite(false);
        return novelWithFavoriteDTO;
    }

    /**
     * 사용자가 작성한 모든 소설 목록을 조회합니다.
     *
     * @param uuid 사용자 UUID
     * @return 해당 사용자의 모든 소설 목록
     */
    public List<NovelWithFavoriteDTO> selectAllNovel(long uuid) {
        return novelRepository.findAllByUuid(uuid);
    }

    /**
     * 특정 소설의 상세 정보를 조회합니다.
     * <p>
     * 조회 전에 {@link #checkOwner(long, long)}를 호출하여 접근 권한을 확인합니다.
     *
     * @param novelNum 조회할 소설의 고유 번호
     * @param uuid     요청 사용자의 UUID
     * @return 소설 상세 정보 {@link Novel}
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닌 경우
     */
    public NovelDTO selectNovelOne(long novelNum, long uuid) throws NoPermissionException {
        checkOwner(novelNum, uuid);
        return NovelDTO.from(novelRepository.findNovelByNovelNum(novelNum));
    }

    /**
     * 특정 소설이 지정된 사용자(UUID)의 소유인지 확인합니다.
     *
     * @param novelNum 소설 번호
     * @param uuid     사용자 UUID
     * @throws NoPermissionException 소설이 해당 사용자의 것이 아닌 경우
     */
    public void checkOwner(long novelNum, long uuid) throws NoPermissionException {
        if (!novelRepository.existsByNovelNumAndUser_Uuid(novelNum, uuid)) {
            throw new NoPermissionException("해당 유저의 소설이 아닙니다.");
        }
    }

    /**
     * 특정 소설의 즐겨찾기 상태를 토글(추가/삭제)합니다.
     * <p>
     * 즐겨찾기가 존재하지 않으면 추가하고, 이미 존재하면 삭제합니다.
     *
     * @param novelNum 소설 번호
     * @param uuid     사용자 UUID
     */
    public void setFavoriteNovel(long novelNum, long uuid) {
        Favorite favorite = new Favorite();

        User user = userService.getUserProxy(uuid);
        FavoriteId fi = new FavoriteId();

        favorite.setUser(user);
        fi.setUuid(uuid);
        fi.setTargetId(novelNum);
        fi.setTargetType("Novel");
        favorite.setId(fi);
        favoriteService.setFavorite(favorite);
    }

    /**
     * 소설 정보를 수정합니다.
     * <p>
     * 수정 전에 {@link #checkOwner(long, long)}를 호출하여 권한을 검증합니다.
     *
     * @param novel 수정할 소설 데이터
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닌 경우
     */
    @Transactional
    public void updateNovel(Novel novel) throws NoPermissionException {
        try {
            checkOwner(novel.getNovelNum(), novel.getUser().getUuid());
            Novel n = novelRepository.findNovelByNovelNum(novel.getNovelNum());
            n.setNovelTitle(novel.getNovelTitle());
        } catch (Exception e) {
            log.warn("Failed to update novel", e);
        }
    }

    public Novel getNovelProxy(long novelNum){
        return novelRepository.getReferenceById(novelNum);
    }

    /**
     * 소설을 삭제합니다.
     * <p>
     * 삭제 전에 {@link #checkOwner(long, long)}를 호출하여 권한을 검증합니다.
     *
     * @param novelNum 삭제할 소설 번호
     * @param uuid 사용자 uuid
     */
    @Transactional
    public void deleteNovel(long novelNum, long uuid) throws NoPermissionException {
        checkOwner(novelNum, uuid);
        Novel n = novelRepository.findNovelByNovelNum(novelNum);
        novelRepository.delete(n);
    }
}
