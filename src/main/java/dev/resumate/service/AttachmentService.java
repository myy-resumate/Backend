package dev.resumate.service;

import dev.resumate.common.s3.S3Util;
import dev.resumate.converter.AttachmentConverter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private static final String FOLDER = "attachment/";

    private final AttachmentRepository attachmentRepository;
    private final S3Util s3Util;

    public Attachment uploadS3AndConvertAttachment(MultipartFile file) throws IOException {

        String uploadKey = FOLDER + UUID.randomUUID() + "_" + file.getOriginalFilename();  //고유한 키 생성
        S3Resource s3Resource = s3Util.uploadObject(file, uploadKey);
        return AttachmentConverter.toAttachment(s3Resource.getURL().toString(), uploadKey, file.getOriginalFilename());
    }

    //s3, db에서 삭제 후, 다시 업로드, 저장
    public List<Attachment> updateFile(List<MultipartFile> files, Resume resume) throws IOException{

        //s3에서 삭제
        deleteFromS3(resume);

        attachmentRepository.deleteAllByResume(resume);
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            attachments.add(uploadS3AndConvertAttachment(file));
        }
        return attachments;
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
