package dev.resumate.service;

import dev.resumate.common.redis.RedisUtil;
import dev.resumate.common.redis.domain.RecentResume;
import dev.resumate.common.redis.repository.RecentResumeRepository;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.dto.DeadlineDTO;
import dev.resumate.repository.dto.TagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HomeServiceTest {

    @InjectMocks
    private HomeService homeService;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private RecentResumeRepository recentResumeRepository;
    private Member member;
    private Resume resume;
    private List<Resume> resumeList;

    @BeforeEach
    void init() {
        this.member = createTestMember();
        this.resume = createTestResume(member);
        this.resumeList = new ArrayList<>();
        resumeList.add(resume);
    }

    @Test
    @DisplayName("캘린더 월별 조회 성공")
    void getCalendar() {
        //given
        when(resumeRepository.findResumeByApplyEndAndMember(any(), any(), any())).thenReturn(resumeList);

        //when
        HomeResponseDTO.CalendarDTO calendarDTO = homeService.getCalendar(member, LocalDate.now());

        //then
        HomeResponseDTO.DateDTO dateDTO = calendarDTO.getDateDTOS().get(0);
        assertThat(dateDTO.getOrganization()).isEqualTo(resumeList.get(0).getOrganization());
        assertThat(dateDTO.getApplyEnd()).isEqualTo(resumeList.get(0).getApplyEnd());
    }

    @Test
    @DisplayName("마감 공고 조회 성공")
    void getDeadline() {
        //given
        List<DeadlineDTO> deadlineDTOS = new ArrayList<>();
        deadlineDTOS.add(DeadlineDTO.builder()
                .organization("테스트 조직")
                .orgUrl("https://test.com")
                .build());
        when(resumeRepository.findDeadlineResume(any(), any(), any())).thenReturn(deadlineDTOS);

        //when
        HomeResponseDTO.DeadlineListDTO deadlineListDTO = homeService.getDeadline(member);

        //then
        HomeResponseDTO.DeadlineDTO deadlineDTO= deadlineListDTO.getDeadlineDTOS().get(0);
        assertThat(deadlineDTO.getOrganization()).isEqualTo(deadlineDTOS.get(0).getOrganization());
        assertThat(deadlineDTO.getOrgUrl()).isEqualTo(deadlineDTOS.get(0).getOrgUrl());
    }

    @Test
    @DisplayName("최근 본 지원서 저장 성공")
    void addRecentResume() {
        //given
        List<TagDTO> tagDTOS = new ArrayList<>();
        tagDTOS.add(TagDTO.builder()
                .taggingId(1L)
                .tagName("테스트 태그")
                .build());
        ReflectionTestUtils.setField(resume, "createdAt", LocalDate.now().atStartOfDay());  //스프링에서 제공하는 필드값 강제 세팅 메소드
        when(redisUtil.addSortedSet(member.getId().toString(), System.currentTimeMillis() / 1000.0, resume.getId().toString(), 5)).thenReturn(new HashSet<>());

        //when & then
        assertDoesNotThrow(() -> homeService.addRecentResume(tagDTOS, resume, member));
    }

    @Test
    @DisplayName("최근 본 지원서 조회 성공")
    void getRecentResume() {
        //given
        Set<String> resumeIdSet = new HashSet<>();
        resumeIdSet.add("1");
        when(redisUtil.getSortedSet(member.getId().toString(), 5)).thenReturn(resumeIdSet);
        when(recentResumeRepository.findById(any())).thenReturn(Optional.ofNullable(RecentResume.builder()
                .resumeId(1L)
                .thumbnail("test recent resume thumbnail")
                .build()));
        List<ResumeResponseDTO.ReadThumbnailDTO> readThumbnailDTOS = new ArrayList<>();
        readThumbnailDTOS.add(ResumeResponseDTO.ReadThumbnailDTO.builder()
                        .resumeId(1L)
                        .title("test title")
                        .createDate(LocalDate.now())
                        .organization("test org")
                        .applyStart(LocalDate.now())
                        .applyEnd(LocalDate.now())
                        .build());
        when(redisUtil.toDTO(any(), any())).thenReturn((List) readThumbnailDTOS);

        //when
        List<ResumeResponseDTO.ReadThumbnailDTO> result = homeService.getRecentResume(member);

        //then
        ResumeResponseDTO.ReadThumbnailDTO thumbnailDTO = result.get(0);
        assertThat(thumbnailDTO.getTitle()).isEqualTo(readThumbnailDTOS.get(0).getTitle());
        assertThat(thumbnailDTO.getOrganization()).isEqualTo(readThumbnailDTOS.get(0).getOrganization());
    }

    private Member createTestMember() {
        return Member.builder()
                .id(1L)
                .name("테스트")
                .email("test@email.com")
                .password("testPassword")
                .build();
    }

    private Resume createTestResume(Member member) {
        return Resume.builder()
                .id(1L)
                .member(member)
                .organization("테스트 조직")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .title("테스트 지원서")
                .orgUrl("https://test.com")
                .build();
    }
}