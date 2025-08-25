package dev.resumate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.ResumeSearch;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.dto.CoverLetterDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
    private TaggingService taggingService;
    @Mock
    private HomeService homeService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private CoverLetterService coverLetterService;

    @Test
    @DisplayName("지원서 저장 성공")
    void saveResume() throws IOException {
        //given
        Member member = createTestMember();
        ResumeRequestDTO.CreateDTO request = ResumeRequestDTO.CreateDTO.builder()
                .title("테스트 제목")
                .organization("테스트 조직")
                .orgURl("https://test.com")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .coverLetterDTOS(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();
        List<MultipartFile> files = new ArrayList<>();
        when(resumeRepository.save(any())).thenReturn(createTestResume(member));

        //when
        ResumeResponseDTO.CreateResultDTO result = resumeService.saveResume(member, request, files);

        //then
        assertThat(result.getResumeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지원서 수정 성공")
    void updateResume() throws IOException {
        //given
        Member member = createTestMember();
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
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.ofNullable(createTestResume(member)));

        //when
        ResumeResponseDTO.UpdateResultDTO result = resumeService.updateResume(member, resumeId, request, files);

        //then
        assertThat(result.getResumeId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지원서 삭제 성공")
    void deleteResume() {
        //given
        Long resumeId = 1L;
        Member member = createTestMember();
        Resume resume = createTestResume(member);
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.ofNullable(resume));

        //when & then
        assertDoesNotThrow(() -> resumeService.deleteResume(resumeId));
        verify(resumeRepository).deleteById(resumeId);
        verify(taggingService).deleteTagging(resume);
        verify(attachmentService).deleteFromS3(resume);
    }

    @Test
    @DisplayName("지원서 상세 조회 성공")
    void readResume() throws JsonProcessingException {
        //given
        Member member = createTestMember();
        Long resumeId = 1L;
        Resume resume = createTestResume(member);
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
    void readResumeList() {
    }

    @Test
    void getResumesByTags() {
    }

    @Test
    void getResumesByKeyword() {
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

        return Resume.builder()
                .id(1L)
                .member(member)
                .organization("테스트 조직")
                .applyStart(LocalDate.now())
                .applyEnd(LocalDate.now())
                .title("테스트 지원서")
                .orgUrl("https://test.com")
                .resumeSearch(resumeSearch)
                .build();
    }
}