package dev.resumate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.resumate.common.redis.RedisUtil;
import dev.resumate.converter.HomeConverter;
import dev.resumate.converter.ResumeConverter;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.dto.DeadlineDTO;
import dev.resumate.repository.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HomeService {

    private static final int MAX_RECENT_RESUME = 5;  //최대 5개까지 조회

    private final ResumeRepository resumeRepository;
    private final RedisUtil redisUtil;

    /**
     * 캘린더 월별 조회
     * @param member
     * @param startDate
     * @return
     */
    public HomeResponseDTO.CalendarDTO getCalendar(Member member, LocalDate startDate) {

        LocalDate endDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.lengthOfMonth());
        List<Resume> resumes = resumeRepository.findResumeByApplyEndAndMember(member, startDate, endDate);

        return HomeResponseDTO.CalendarDTO.builder()
                .dateDTOS(HomeConverter.toDateDTO(resumes))
                .build();
    }

    /**
     * 마감 공고 조회
     * @param member
     * @return
     */
    public HomeResponseDTO.DeadlineListDTO getDeadline(Member member) {

        //상위 5개만 조회하기 위한 PageRequest 구현체
        List<DeadlineDTO> deadlineDTOS = resumeRepository.findDeadlineResume(member, LocalDate.now(), PageRequest.of(0, 5));

        return HomeResponseDTO.DeadlineListDTO.builder()
                .deadlineDTOS(HomeConverter.toDeadlineDTO(deadlineDTOS))
                .build();
    }

    /**
     * 최근 본 지원서 redis에 저장
     * @param tags
     * @param resume
     * @param member
     * @throws JsonProcessingException
     */
    public void addRecentResume(List<TagDTO> tags, Resume resume, Member member) throws JsonProcessingException {

        ResumeResponseDTO.ReadThumbnailDTO thumbnailDTO = ResumeConverter.toReadThumbnailDTO(resume, tags);
        //dto를 json으로 직렬화
        String jsonMember = redisUtil.toJson(thumbnailDTO);
        redisUtil.addSortedSet(member.getId().toString(), System.currentTimeMillis() / 1000.0, jsonMember, MAX_RECENT_RESUME);
    }

    /**
     * 태그를 dto로 변환
     * @param tags
     * @return
     */
    public List<TagDTO> toTagDTOList(List<String> tags) {
         return tags.stream().map(tag -> TagDTO.builder()
                 .tagName(tag)
                 .build()).toList();
    }

    /**
     * 최근 본 지원서 조회
     * @param member
     * @return
     */
    public List<ResumeResponseDTO.ReadThumbnailDTO> getRecentResume(Member member) {

        Set<String> json = redisUtil.getSortedSet(member.getId().toString(), MAX_RECENT_RESUME);
        return redisUtil.toDTO(json, ResumeResponseDTO.ReadThumbnailDTO.class);
    }
}
