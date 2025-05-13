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
import dev.resumate.repository.TaggingRepository;
import dev.resumate.repository.dto.AttachmentDTO;
import dev.resumate.repository.dto.CoverLetterDTO;
import dev.resumate.repository.dto.ResumeDTO;
import dev.resumate.repository.dto.TagDTO;
import io.pinecone.configs.PineconeConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AttachmentService attachmentService;
    private final TaggingService taggingService;
    private final CoverLetterService coverLetterService;
    private final HomeService homeService;
    private final VectorStore vectorStore;

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
                Attachment attachment = attachmentService.uploadS3AndConvertAttachment(file, resume.getTitle());
                resume.addAttachment(attachment);
            }
        }

        Resume newResume = resumeRepository.save(resume);  //cascade로 저장

        //태그 저장은 따로
        taggingService.saveTagAndTagging(request.getTags(), member, newResume);

        //redis에 최근 본 지원서로 저장
        homeService.addRecentResume(homeService.toTagDTOList(request.getTags()), newResume, member);

        //벡터db에 자소서 질문 저장
        saveQuestionVector(member, newResume);

        return ResumeResponseDTO.CreateResultDTO.builder()
                .resumeId(newResume.getId())
                .build();
    }

    //자소서 질문을 벡터db에 저장
    private void saveQuestionVector(Member member, Resume resume) {
        if (resume.getCoverLetters().isEmpty()) {
            return;
        }
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("member_id", member.getId());
        metaData.put("resume_id", resume.getId());
        List<Document> documentList = resume.getCoverLetters().stream()
                .filter(coverLetter -> !coverLetter.getQuestion().isEmpty())  //빈 질문은 거르기
                .map(coverLetter -> {
            metaData.put("cover_letter_id", coverLetter.getId());
            return new Document(coverLetter.getId().toString(), coverLetter.getQuestion(), metaData);  //자소서의 id로 벡터 id 지정
        }).toList();
        vectorStore.add(documentList);
    }

    //지원서 수정
    @Transactional
    public ResumeResponseDTO.UpdateResultDTO updateResume(Member member, Long resumeId, ResumeRequestDTO.UpdateDTO request, List<MultipartFile> files) throws IOException {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));

        //벡터db에서 기존 자소서 질문 벡터 삭제
        deleteQuestionVector(resume);

        coverLetterService.updateCoverLetters(request.getCoverLetterDTOS(), resume);  //자소서 수정, 벡터db의 자소서 질문 벡터도 수정
        attachmentService.updateFiles(files, resume);  //첨부 파일 수정
        resume.setResume(request);  //지원서 수정
        taggingService.updateTagging(request.getTags(), member, resume);  //태깅 수정

        //redis에 최근 본 지원서로 저장
        homeService.addRecentResume(request.getTags(), resume, member);

        //벡터db에 다시 저장
        saveQuestionVector(member, resume);

        return ResumeResponseDTO.UpdateResultDTO.builder()
                .resumeId(resume.getId())
                .build();
    }

    //지원서 삭제
    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BusinessBaseException(ErrorCode.RESUME_NOT_FOUND));
        //태깅은 cascade 안했으므로 따로 삭제
        taggingService.deleteTagging(resume);
        //첨부파일 s3에서 삭제
        attachmentService.deleteFromS3(resume);

        //벡터db에서 자소서 질문 벡터 삭제
        deleteQuestionVector(resume);

        resumeRepository.deleteById(resume.getId());
    }

    private void deleteQuestionVector(Resume resume) {
        List<String> ids = resume.getCoverLetters().stream().map(coverLetter -> coverLetter.getId().toString()).toList();
        if (!ids.isEmpty()) {
            vectorStore.delete(ids);
        }
    }

    //지원서 상세 조회
    public ResumeResponseDTO.ReadResultDTO readResume(Member member, Long resumeId) throws JsonProcessingException {

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
