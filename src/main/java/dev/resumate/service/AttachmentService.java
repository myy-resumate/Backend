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

    private final AttachmentRepository attachmentRepository;
    private final S3Util s3Util;

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
