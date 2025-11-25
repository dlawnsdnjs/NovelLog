package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.Episode.EpisodeDTO;
import com.example.novelcharacter.mapper.EpisodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;

/**
 * 회차(Episode) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>이 클래스는 {@link EpisodeMapper}를 통해 데이터베이스와 연동하며,
 * {@link NovelService}를 이용해 사용자의 소유권 검증을 수행합니다.
 * 회차 등록, 수정, 삭제, 검색 등의 기능을 제공합니다.</p>
 *
 * @author
 * @since 2025-10-15
 */
@Service
public class EpisodeService {

    /** 회차 관련 데이터베이스 작업을 수행하는 매퍼 */
    private final EpisodeMapper episodeMapper;

    /** 소설의 소유자 검증 및 관련 검증 로직을 담당하는 서비스 */
    private final NovelService novelService;

    /**
     * EpisodeService 생성자.
     *
     * @param episodeMapper 회차 데이터 접근 매퍼
     * @param novelService  소설 서비스 (소유자 검증용)
     */
    @Autowired
    public EpisodeService(EpisodeMapper episodeMapper, NovelService novelService) {
        this.episodeMapper = episodeMapper;
        this.novelService = novelService;
    }

    /**
     * 새로운 회차를 등록합니다.
     *
     * <p>소설 소유자만 등록할 수 있으며, 등록 후 회차 순서(order index)를 보정합니다.</p>
     *
     * @param episodeDTO 등록할 회차 정보
     * @param uuid       사용자 고유 식별자(UUID)
     * @return 등록된 회차 정보
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    public EpisodeDTO insertEpisode(EpisodeDTO episodeDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(episodeDTO.getNovelNum(), uuid);
        episodeMapper.insertEpisode(episodeDTO);
        episodeMapper.updateOrderIndexNull(episodeDTO);
        return episodeDTO;
    }

    /**
     * 특정 소설의 모든 회차 목록을 조회합니다.
     *
     * @param novelNum 조회할 소설 번호
     * @param uuid     사용자 UUID
     * @return 회차 목록
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    public List<EpisodeDTO> selectAllEpisode(long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return episodeMapper.selectAllEpisode(novelNum);
    }

    /**
     * 회차 목록을 페이징하여 조회합니다.
     *
     * @param novelNum 소설 번호
     * @param offset   조회 시작 위치
     * @param uuid     사용자 UUID
     * @return 페이징된 회차 목록
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    public List<EpisodeDTO> selectEpisodePage(long novelNum, int offset, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return episodeMapper.selectEpisodePage(novelNum, offset);
    }

    /**
     * 특정 회차의 소유권을 확인합니다.
     *
     * @param episodeNum 회차 번호
     * @param uuid       사용자 UUID
     * @return 소유자가 맞을 경우 1 반환
     * @throws NoPermissionException 사용자가 해당 회차의 소유자가 아닌 경우
     */
    public int checkEpisodeOwner(long episodeNum, long uuid) throws NoPermissionException {
        if (episodeMapper.checkEpisodeOwner(episodeNum, uuid) == 1) {
            return 1;
        } else {
            throw new NoPermissionException("사용자가 작성한 회차가 아닙니다.");
        }
    }

    /**
     * 특정 소설 내에서 회차를 검색합니다.
     *
     * <p>검색은 제목 등 일부 문자열을 기준으로 수행됩니다.</p>
     *
     * @param search   검색어
     * @param novelNum 소설 번호
     * @param uuid     사용자 UUID
     * @return 검색 결과로 반환된 회차 목록
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    public List<EpisodeDTO> searchEpisode(String search, long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return episodeMapper.searchEpisode(search);
    }

    /**
     * 회차 정보를 수정합니다.
     *
     * @param episodeDTO 수정할 회차 정보
     * @param uuid       사용자 UUID
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    public void updateEpisode(EpisodeDTO episodeDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(episodeDTO.getNovelNum(), uuid);
        episodeMapper.updateEpisode(episodeDTO);
    }

    /**
     * 회차를 삭제합니다.
     *
     * <p>삭제 전 소유권을 검증합니다.</p>
     *
     * @param episodeNum 삭제할 회차 번호
     * @param uuid       사용자 UUID
     * @throws NoPermissionException 사용자가 해당 회차의 소유자가 아닌 경우
     */
    public void deleteEpisode(long episodeNum, long uuid) throws NoPermissionException {
        checkEpisodeOwner(episodeNum, uuid);
        episodeMapper.deleteEpisode(episodeNum);
    }
}
