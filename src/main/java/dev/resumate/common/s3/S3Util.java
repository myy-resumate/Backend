package dev.resumate.common.s3;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Operations s3Operations;

    //s3에 업로드
    public S3Resource uploadObject(MultipartFile file, String uploadKey) throws IOException {
        InputStream inputStream = file.getInputStream();
        return s3Operations.upload(bucketName, uploadKey, inputStream, ObjectMetadata.builder().contentType(file.getContentType()).build());
    }


    //s3에서 object 삭제
    public void deleteObject(String uploadKey) {

        s3Operations.deleteObject(bucketName, uploadKey);
    }
}
