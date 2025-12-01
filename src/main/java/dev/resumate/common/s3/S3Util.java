package dev.resumate.common.s3;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Util {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Operations s3Operations;
    private final S3Presigner s3Presigner;

    /**
     * presigned url 발급
     * @param uploadKey
     * @param contentType
     * @return
     */
    public String getPresignedUrl(String uploadKey, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  //유효시간 10분
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();
        s3Presigner.close();
        return presignedUrl;
    }

    /**
     * S3 오브젝트 삭제
     * @param uploadKey
     */
    public void deleteObject(String uploadKey) {

        s3Operations.deleteObject(bucketName, uploadKey);
    }

    /**
     * S3 오브젝트 다운로드
     * @param key
     * @return
     */
    public S3Resource downloadObject(String key) {

        return s3Operations.download(bucketName, key);
    }
}
