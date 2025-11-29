package dev.resumate.service;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.common.s3.S3Util;
import dev.resumate.converter.AttachmentConverter;
import dev.resumate.domain.Attachment;
import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.repository.AttachmentRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private static final String FOLDER = "attachment/";

    private final AttachmentRepository attachmentRepository;
    private final S3Util s3Util;

    public Attachment uploadS3AndConvertAttachment(MultipartFile file, String resumeTitle) throws IOException {

        String uploadKey = FOLDER + resumeTitle + UUID.randomUUID();  //고유한 키 생성
        //S3Resource s3Resource = s3Util.uploadObject(file, uploadKey);
        return AttachmentConverter.toAttachment(uploadKey, file.getOriginalFilename());
    }

    //첨부파일 수정
    @Transactional
    public void updateFiles(List<MultipartFile> files, Resume resume) throws IOException{

        List<Attachment> oldAttachments = attachmentRepository.findAllByResume(resume);

        if (files == null) { //files가 null인 경우 그냥 삭제
            oldAttachments.forEach(oldAttachment -> s3Util.deleteObject(oldAttachment.getUploadKey()));
            resume.getAttachments().removeIf(oldAttachments::contains);
        } else {
            Iterator<MultipartFile> fileIterator = files.iterator();

            for (Attachment oldAttachment : oldAttachments) {
                if (fileIterator.hasNext()) {
                    MultipartFile file = fileIterator.next();
                    oldAttachment.setFileName(file.getOriginalFilename());
                    //s3Util.uploadObject(file, oldAttachment.getUploadKey());
                } else {  //더 이상 바꿀 file이 없으면 남은 기존 파일은 삭제
                    s3Util.deleteObject(oldAttachment.getUploadKey());
                    resume.getAttachments().remove(oldAttachment);
                }
            }

            //기존보다 추가된 file이 많은 경우
            while (fileIterator.hasNext()) {
                MultipartFile file = fileIterator.next();
                resume.addAttachment(uploadS3AndConvertAttachment(file, resume.getTitle()));
            }
        }
    }

    //첨부파일 s3에서 삭제
    public void deleteFromS3(Resume resume) {

        //s3에서 삭제
        List<Attachment> attachments = attachmentRepository.findAllByResume(resume);
        for (Attachment attachment : attachments) {
            s3Util.deleteObject(attachment.getUploadKey());
        }
    }

    //첨부파일 다운로드
    public ResponseEntity<?> fileDownload(Long attachmentId) throws IOException {

        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new BusinessBaseException(ErrorCode.FILE_NOT_FOUND));

        try {
            S3Resource s3Resource = s3Util.downloadObject(attachment.getUploadKey());
            HttpHeaders httpHeaders = getHttpHeaders(s3Resource);
            //서버에 부담가지 않도록 파일을 메모리에 올리지 않고 스트림으로 반환
            return new ResponseEntity<>(new InputStreamResource(s3Resource.getInputStream()), httpHeaders, HttpStatus.OK);
        } catch (IOException e) {  //에러 응답은 dto로
            return new ResponseEntity<>(new ApiResponseDTO<>(ErrorCode.FILE_DOWNLOAD_ERROR.getCode(), ErrorCode.FILE_DOWNLOAD_ERROR.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    //헤더 설정
    private HttpHeaders getHttpHeaders(S3Resource s3Resource) throws IOException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType(s3Resource.contentType()));
        return httpHeaders;
    }

}
