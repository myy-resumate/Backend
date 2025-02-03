package dev.resumate.service;

import dev.resumate.common.s3.S3Util;
import dev.resumate.domain.Attachment;
import dev.resumate.domain.Resume;
import dev.resumate.repository.AttachmentRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String FOLDER = "attachment/";

    private final AttachmentRepository attachmentRepository;
    private final S3Operations s3Operations;
    private final S3Util s3Util;

    //첨부파일 저장
    private void saveAttachment(String fileName, String uploadKey, String url, Resume resume) {

        Attachment attachment = Attachment.builder()
                .fileName(fileName)
                .uploadKey(uploadKey)
                .url(url)
                .resume(resume)
                .build();

        attachmentRepository.save(attachment);
    }

    //s3 업로드
    public void uploadS3AndSaveUrl(List<MultipartFile> files, Resume resume) throws IOException {

        for (MultipartFile file : files) {
            UUID uuid = UUID.randomUUID();  //고유한 이름 생성
            String uploadKey = FOLDER + uuid + "_" + file.getOriginalFilename();
            S3Resource s3Resource = s3Util.uploadObject(file, uploadKey);
            saveAttachment(file.getOriginalFilename(), uploadKey, s3Resource.getURL().toString(), resume);
        }
    }

    //s3, db에서 삭제 후, 다시 업로드, 저장
    @Transactional
    public void updateFile(List<MultipartFile> files, Resume resume) throws IOException{

        //db에서 삭제
        attachmentRepository.deleteAllByResume(resume);
        //s3에서 삭제
        deleteFromS3(resume);

        uploadS3AndSaveUrl(files, resume);
    }

    //첨부파일 s3에서 삭제
    public void deleteFromS3(Resume resume) {

        //s3에서 삭제
        List<Attachment> attachments = attachmentRepository.findAllByResume(resume);
        for (Attachment attachment : attachments) {
            s3Util.deleteObject(attachment.getUploadKey());
        }
    }
}
