package dev.resumate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.resumate.common.redis.RedisUtil;
import dev.resumate.common.redis.repository.RecentResumeRepository;
import dev.resumate.common.slice.SliceUtil;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.ResumeSearch;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.AttachmentRepository;
import dev.resumate.repository.CoverLetterRepository;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.TaggingRepository;
import dev.resumate.repository.dto.CoverLetterDTO;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ResumeServiceTest {
    @InjectMocks
    private ResumeService resumeService;
    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private HomeService homeService;
    @Mock
    private RedisUtil redisUtil;
    @Mock
    private RecentResumeRepository recentResumeRepository;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private TaggingRepository taggingRepository;
    @Mock
    private CoverLetterRepository coverLetterRepository;

    private Member member;
    private Resume resume;
    private List<Resume> resumes;

    //초기화 - 테스트 전에 먼저 실행됨
    @BeforeEach
    void init() {
        this.member = createTestMember();
        this.resume = createTestResume(member);
        this.resumes = new ArrayList<>();
        resumes.add(resume);
    }

    @Test
    @DisplayName("지원서 저장 성공")
    void saveResume() throws IOException {
        //given
        ResumeRequestDTO.CreateDTO request = ResumeRequestDTO.CreateDTO.builder()
                .title("테스트 제목")
                .organization("테스트 조직")
                .orgURl("https://test.com")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .coverLetterDTOS(new ArrayList<>())
                .tags(new ArrayList<>())
                .fileDTOS(new ArrayList<>())
                .build();
        when(resumeRepository.save(any())).thenReturn(resume);

        //when
        ResumeResponseDTO.CreateResultDTO result = resumeService.saveResume(member, request);

        //then
        assertThat(result.getResumeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지원서 수정 성공")
    void updateResume() throws IOException {
        //given
        Long resumeId = 1L;
        ResumeRequestDTO.UpdateDTO request = ResumeRequestDTO.UpdateDTO.builder()
                .title("테스트 제목")
                .organization("테스트 조직")
                .orgURl("https://test.com")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .coverLetterDTOS(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();
        List<MultipartFile> files = new ArrayList<>();
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.ofNullable(resume));
        when(coverLetterRepository.findAllByResume(resume)).thenReturn(new ArrayList<>());

        //when
        ResumeResponseDTO.UpdateResultDTO result = resumeService.updateResume(member, resumeId, request);

        //then
        assertThat(result.getResumeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지원서 삭제 성공")
    void deleteResume() {
        //given
        Long resumeId = 1L;
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.ofNullable(resume));

        //when & then
        assertDoesNotThrow(() -> resumeService.deleteResume(member, resumeId));
        verify(resumeRepository).deleteById(resumeId);
        //verify(taggingService).deleteTagging(resume);
    }

    @Test
    @DisplayName("지원서 상세 조회 성공")
    void readResume() throws JsonProcessingException {
        //given
        Long resumeId = 1L;
        List<CoverLetterDTO> coverLetterDTOS = new ArrayList<>();
        coverLetterDTOS.add(CoverLetterDTO.builder()
                .coverLetterId(1L)
                .question("테스트 질문")
                .answer("테스트 답변")
                .build());
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.ofNullable(resume));
        when(resumeRepository.findCoverLetter(resumeId)).thenReturn(coverLetterDTOS);  //stubbing 안하면 빈 컬렉션 반환해서 테스트 실패

        //when
        ResumeResponseDTO.ReadResultDTO result = resumeService.readResume(member, resumeId);

        //then
        assertThat(result.getTitle()).isEqualTo("테스트 지원서");
        assertThat(result.getCoverLetters().get(0).getQuestion()).isEqualTo("테스트 질문");
        assertThat(result.getAttachments()).isNullOrEmpty();  //stubbing 안했으므로 빈 리스트가 반환됨
        assertThat(result.getTags()).isNullOrEmpty();
    }

    @Test
    @DisplayName("지원서 목록 조회 성공")
    void readResumeList() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        when(resumeRepository.findAllResume(member, pageable)).thenReturn(SliceUtil.toSlice(resumes, pageable));

        //when
        Slice<ResumeResponseDTO.ReadThumbnailDTO> result = resumeService.readResumeList(member, pageable);

        //then
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 지원서");
    }

    @Test
    @DisplayName("태그로 검색 성공")
    void getResumesByTags() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        when(resumeRepository.findByTag(member, tags, pageable)).thenReturn(SliceUtil.toSlice(resumes, pageable));

        //when
        Slice<ResumeResponseDTO.ReadThumbnailDTO> result = resumeService.getResumesByTags(member, tags, pageable);

        //then
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 지원서");
    }

    @Test
    @DisplayName("지원서 검색")
    void getResumesByKeyword() {
        //given
        String keyword = "검색 키워드";
        Pageable pageable = PageRequest.of(0, 10);
        when(resumeRepository.findByKeyword(member.getId(), keyword, pageable)).thenReturn(SliceUtil.toSlice(resumes, pageable));

        //when
        Slice<ResumeResponseDTO.ReadThumbnailDTO> result = resumeService.getResumesByKeyword(member, keyword, pageable);

        //then
        assertThat(result.getNumberOfElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 지원서");
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
        ResumeSearch resumeSearch = ResumeSearch.builder()
                .title("테스트 지원서")
                .organization("테스트 조직")
                .questions("테스트 질문")
                .answers("테스트 답변")
                .build();

        Resume resume = Resume.builder()
                .id(1L)
                .member(member)
                .organization("테스트 조직")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .title("테스트 지원서")
                .orgUrl("https://test.com")
                .resumeSearch(resumeSearch)
                .taggings(new ArrayList<>())
                .attachments(new ArrayList<>())
                .coverLetters(new ArrayList<>())
                .build();

        ReflectionTestUtils.setField(resume, "createdAt", LocalDate.now().atStartOfDay());  //resume의 생성시간을 임의로 설정
        return resume;
    }
}