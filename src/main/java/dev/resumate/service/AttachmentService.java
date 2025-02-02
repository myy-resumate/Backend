package dev.resumate.service;

import dev.resumate.domain.Attachment;
import dev.resumate.domain.Resume;
import dev.resumate.repository.AttachmentRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final AttachmentRepository attachmentRepository;
    private final S3Operations s3Operations;

    //첨부파일 저장
    private void saveAttachment(String fileName, String url, Resume resume) {

        Attachment attachment = Attachment.builder()
                .fileName(fileName)
                .url(url)
                .resume(resume)
                .build();

        attachmentRepository.save(attachment);
    }

    //s3 업로드
    public void uploadS3AndSaveUrl(List<MultipartFile> files, Resume resume) throws IOException {

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            UUID uuid = UUID.randomUUID();
            String uploadImageKey = "attachment/" + uuid + "_" + file.getOriginalFilename();  //고유한 이름 생성

            S3Resource s3Resource = s3Operations.upload(bucketName, uploadImageKey, inputStream, ObjectMetadata.builder().contentType(file.getContentType()).build());
            saveAttachment(file.getOriginalFilename(), s3Resource.getURL().toString(), resume);
        }
    }
}
