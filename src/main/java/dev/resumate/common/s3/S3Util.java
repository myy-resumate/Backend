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

    /**
     * S3에 업로드
     * @param uploadKey
     * @return
     * @throws IOException
     */
    @Async("imageUploadExecutor")
    public void uploadObject(InputStream inputStream, String contentType, String uploadKey) throws IOException {

        //로깅 레벨이 info까지로 제한됨 주의
        log.info("Thread upload work start: {}, image: {}",
                Thread.currentThread().getThreadGroup().getName()+ " " + Thread.currentThread().getId(), uploadKey);
        s3Operations.upload(bucketName, uploadKey, inputStream, ObjectMetadata.builder().contentType(contentType).build());
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
