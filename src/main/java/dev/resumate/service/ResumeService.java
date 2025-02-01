package dev.resumate.service;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CoverLetterService coverLetterService;
    private final TagService tagService;
    private final AttachmentService attachmentService;

    /**
     * 지원서 저장
     * @param member
     * @param request
     * @param files
     * @return
     */
    public ResumeResponseDTO.CreateResultDTO saveResume(Member member, ResumeRequestDTO.CreateDTO request, List<MultipartFile> files) {

        Resume resume = Resume.builder()
                .title(request.getTitle())
                .organization(request.getOrganization())
                .orgUrl(request.getOrgURl())
                .applyStart(request.getApplyStart())
                .applyEnd(request.getApplyEnd())
                .member(member)
                .build();

        Resume newResume = resumeRepository.save(resume);

        //자소서 저장
        if (request.getCoverLetterDTOS() != null) {
            coverLetterService.saveCoverLetter(request.getCoverLetterDTOS(), newResume);
        }
        //태그 저장
        if (request.getTags() != null) {
            tagService.saveTag(request.getTags(), member, newResume);
        }
        //첨부파일 저장 - s3업로드 후 url만 저장
        if (files != null) {
            //s3 업로드하는 코드
            attachmentService.saveAttachment("url", newResume);
        }

        return ResumeResponseDTO.CreateResultDTO.builder()
                .resumeId(newResume.getId())
                .build();
    }
}
