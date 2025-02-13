package dev.resumate.service;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.converter.CoverLetterConverter;
import dev.resumate.converter.ResumeConverter;
import dev.resumate.domain.*;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.dto.AttachmentDTO;
import dev.resumate.repository.dto.CoverLetterDTO;
import dev.resumate.repository.dto.ResumeDTO;
import dev.resumate.repository.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final TagService tagService;
    private final AttachmentService attachmentService;
    private final TaggingService taggingService;
    private final CoverLetterService coverLetterService;

    /**
     * 지원서 저장
     * @param member
     * @param request
     * @param files
     * @return
     */
    @Transactional //하나라도 실패하면 전체 롤백
    public ResumeResponseDTO.CreateResultDTO saveResume(Member member, ResumeRequestDTO.CreateDTO request, List<MultipartFile> files) throws IOException {

        Resume resume = ResumeConverter.toResume(request, member);

        //자소서 추가
        for (ResumeRequestDTO.CoverLetterDTO coverLetterDTO : request.getCoverLetterDTOS()) {
            resume.addCoverLetter(CoverLetterConverter.toCoverLetter(coverLetterDTO));
        }

        //첨부파일 추가
        if (files != null) {
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.uploadS3AndConvertAttachment(file);
                resume.addAttachment(attachment);
            }
        }

        Resume newResume = resumeRepository.save(resume);  //cascade로 저장

        //태그 저장은 따로
        tagService.saveTag(request.getTags(), member, newResume);

        return ResumeResponseDTO.CreateResultDTO.builder()
                .resumeId(newResume.getId())
                .build();
    }

    @Transactional
    public ResumeResponseDTO.UpdateResultDTO updateResume(Member member, Long resumeId, ResumeRequestDTO.UpdateDTO request, List<MultipartFile> files) throws IOException {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));

        //자소서 삭제 후 리스트 새로 생성
        coverLetterService.deleteCoverLetters(resume);
        List<CoverLetter> coverLetters = new ArrayList<>();
        for (ResumeRequestDTO.CoverLetterDTO coverLetterDTO : request.getCoverLetterDTOS()) {
            coverLetters.add(CoverLetterConverter.toCoverLetter(coverLetterDTO));
        }

        //첨부파일 수정
        List<Attachment> attachments = new ArrayList<>();
        if (files != null) {
            attachments = attachmentService.updateFile(files, resume);
        }

        resume.setResume(request, coverLetters, attachments);  //cascade로 저장

        //태그 수정
        if (request.getTags() != null) {
            tagService.updateTag(request.getTags(), member, resume);
        }

        return ResumeResponseDTO.UpdateResultDTO.builder()
                .resumeId(resume.getId())
                .build();
    }

    //지원서 삭제
    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        resumeRepository.deleteById(resumeId);
        //태깅은 cascade 안했으므로 따로 삭제
        taggingService.deleteTagging(resume);
        //첨부파일 s3에서 삭제
        attachmentService.deleteFromS3(resume);
    }

    //지원서 상세 조회
    public ResumeResponseDTO.ReadResultDTO readResume(Long resumeId) {

        ResumeDTO resumeDTO = resumeRepository.findResume(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        List<CoverLetterDTO> coverLetterDTOS = resumeRepository.findCoverLetter(resumeId);
        List<AttachmentDTO> attachmentDTOS = resumeRepository.findAttachment(resumeId);
        List<TagDTO> tagDTOS = resumeRepository.findTag(resumeId);

        return ResumeConverter.toReadResultDTO(resumeDTO, coverLetterDTOS, attachmentDTOS, tagDTOS);
    }

    //지원서 목록 조회
    public Slice<ResumeResponseDTO.ReadThumbnailDTO> readResumeList(Member member, Pageable pageable) {

        Slice<Resume> resumes = resumeRepository.findAllResume(member, pageable);

        return resumes.map(
                resume -> {
                    List<TagDTO> tagDTOS = resume.getTaggings().stream()
                            .map(tagging -> TagDTO.builder()
                                    .tagName(tagging.getTag().getName())
                                    .build())
                            .collect(Collectors.toList());
                    return ResumeConverter.toReadThumbnailDTO(resume, tagDTOS);
                });
    }
}
