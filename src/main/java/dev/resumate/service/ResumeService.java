package dev.resumate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.common.redis.RedisUtil;
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
    private final HomeService homeService;

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

        //redis에 최근 본 지원서로 저장
        homeService.addRecentResume(homeService.toTagDTOList(request.getTags()), newResume, member);

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

        //redis에 최근 본 지원서로 저장
        homeService.addRecentResume(homeService.toTagDTOList(request.getTags()), resume, member);

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
    public ResumeResponseDTO.ReadResultDTO readResume(Member member, Long resumeId) throws JsonProcessingException {

        //ResumeDTO resumeDTO = resumeRepository.findResume(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        List<CoverLetterDTO> coverLetterDTOS = resumeRepository.findCoverLetter(resumeId);
        List<AttachmentDTO> attachmentDTOS = resumeRepository.findAttachment(resumeId);
        List<TagDTO> tagDTOS = resumeRepository.findTag(resumeId);

        //redis에 최근 본 지원서로 저장
        homeService.addRecentResume(tagDTOS, resume, member);

        return ResumeConverter.toReadResultDTO(resume, coverLetterDTOS, attachmentDTOS, tagDTOS);
    }

    //지원서 목록 조회
    public Slice<ResumeResponseDTO.ReadThumbnailDTO> readResumeList(Member member, Pageable pageable) {

        Slice<Resume> resumes = resumeRepository.findAllResume(member, pageable);
        return ResumeConverter.mapReadThumbnailDTO(resumes);
    }

    //태그로 검색
    public Slice<ResumeResponseDTO.ReadThumbnailDTO> getResumesByTags(Member member, List<String> tags, Pageable pageable) {

        Slice<Resume> resumes = resumeRepository.findByTag(member, tags, tags.size(), pageable);
        return ResumeConverter.mapReadThumbnailDTO(resumes);
    }

    //지원서 검색
    public Slice<ResumeResponseDTO.ReadThumbnailDTO> getResumesByKeyword(Member member, String keyword, Pageable pageable) {

        Slice<Resume> resumes = resumeRepository.findByKeyword(member.getId(), keyword, pageable);
        return ResumeConverter.mapReadThumbnailDTO(resumes);
    }
}
