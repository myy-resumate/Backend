package dev.resumate.service;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CoverLetterService coverLetterService;
    private final TagService tagService;
    private final AttachmentService attachmentService;
    private final TaggingService taggingService;

    /**
     * 지원서 저장
     * @param member
     * @param request
     * @param files
     * @return
     */
    @Transactional //하나라도 실패하면 전체 롤백
    public ResumeResponseDTO.CreateResultDTO saveResume(Member member, ResumeRequestDTO.CreateDTO request, List<MultipartFile> files) throws IOException {

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
        //첨부파일 저장
        if (files != null) {
            //s3 업로드하고, url 저장
            attachmentService.uploadS3AndSaveUrl(files, newResume);
        }

        return ResumeResponseDTO.CreateResultDTO.builder()
                .resumeId(newResume.getId())
                .build();
    }

    @Transactional
    public ResumeResponseDTO.UpdateResultDTO updateResume(Member member, Long resumeId, ResumeRequestDTO.UpdateDTO request, List<MultipartFile> files) throws IOException {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        resume.setResume(request);

        //자소서 수정
        if (request.getCoverLetterDTOS() != null) {
            coverLetterService.updateCoverLetter(request.getCoverLetterDTOS(), resume);
        }
        //태그 수정
        if (request.getTags() != null) {
            tagService.updateTag(request.getTags(), member, resume);
        }
        //첨부파일 수정
        if (files != null) {
            //s3 업로드하고, url 저장
            attachmentService.updateFile(files, resume);
        }

        return ResumeResponseDTO.UpdateResultDTO.builder()
                .resumeId(resume.getId())
                .build();
    }

    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        resumeRepository.deleteById(resumeId);
        //태깅은 cascade 안했으므로 따로 삭제
        taggingService.deleteTagging(resume);
        //첨부파일 s3에서 삭제
        attachmentService.deleteFromS3(resume);
    }
}
