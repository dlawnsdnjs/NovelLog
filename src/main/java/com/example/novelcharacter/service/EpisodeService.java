package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.Episode.dto.EpisodeDTO;
import com.example.novelcharacter.domain.Episode.entity.Episode;
import com.example.novelcharacter.domain.Novel.entity.Novel;
import com.example.novelcharacter.repository.EpisodeRepository;
import com.example.novelcharacter.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EpisodeService {

    /** 회차 관련 데이터베이스 작업을 수행하는 매퍼 */
    private final EpisodeRepository episodeRepository;

    /** 소설의 소유자 검증 및 관련 검증 로직을 담당하는 서비스 */
    private final NovelService novelService;
    private final NovelRepository novelRepository;

    /**
     * 새로운 회차를 등록합니다.
     *
     * <p>소설 소유자만 등록할 수 있으며, 등록 후 회차 순서(order index)를 보정합니다.</p>
     *
     * @param episode 등록할 회차 정보
     * @param uuid       사용자 고유 식별자(UUID)
     * @return 등록된 회차 정보
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    @Transactional
    public EpisodeDTO insertEpisode(EpisodeDTO episode, long uuid) throws NoPermissionException {
        novelService.checkOwner(episode.getNovelNum(), uuid);
        Episode newEpisode = new Episode();
        newEpisode.setEpisodeNum(episode.getEpisodeNum());
        newEpisode.setEpisodeTitle(episode.getEpisodeTitle());
        Novel n = novelService.getNovelProxy(episode.getNovelNum());
        newEpisode.setNovel(n);
        newEpisode.setEpisodeSummary(episode.getEpisodeSummary());
        Episode result = episodeRepository.save(newEpisode);
        result.setOrderIndex(result.getEpisodeNum());
        return EpisodeDTO.from(result);
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
        return episodeRepository.findAllEpisodesByNovel_NovelNum(novelNum).stream().map(EpisodeDTO::from).collect(Collectors.toList());
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
        Pageable pageable = PageRequest.of(offset-1, 20, Sort.by("orderIndex").descending());
        return episodeRepository.findEpisodeByNovel_NovelNum(novelNum, pageable).stream().map(EpisodeDTO::from).collect(Collectors.toList());
    }

    /**
     * 특정 회차의 소유권을 확인합니다.
     *
     * @param episodeNum 회차 번호
     * @param uuid       사용자 UUID
     * @throws NoPermissionException 사용자가 해당 회차의 소유자가 아닌 경우
     */
    public void checkEpisodeOwner(long episodeNum, long uuid) throws NoPermissionException {
        if (!episodeRepository.existsByEpisodeNumAndUuid(episodeNum, uuid)) {
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
    public List<Episode> searchEpisode(String search, long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return episodeRepository.findByEpisodeTitleContaining(search);
    }

    /**
     * 회차 정보를 수정합니다.
     *
     * @param episode 수정할 회차 정보
     * @param uuid       사용자 UUID
     * @throws NoPermissionException 사용자가 해당 소설의 소유자가 아닌 경우
     */
    @Transactional
    public void updateEpisode(EpisodeDTO episode, long uuid) throws NoPermissionException {
        novelService.checkOwner(episode.getNovelNum(), uuid);
        Episode e = episodeRepository.findEpisodeByEpisodeNum(episode.getEpisodeNum());
        e.setEpisodeTitle(episode.getEpisodeTitle());
        e.setOrderIndex(episode.getOrderIndex());
        e.setEpisodeSummary(episode.getEpisodeSummary());
    }

    public Episode getEpisodeProxy(long episodeNum){
        return episodeRepository.getReferenceById(episodeNum);
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
    @Transactional
    public void deleteEpisode(long episodeNum, long uuid) throws NoPermissionException {
        checkEpisodeOwner(episodeNum, uuid);
        Episode e = episodeRepository.findEpisodeByEpisodeNum(episodeNum);
        episodeRepository.delete(e);
    }
}
