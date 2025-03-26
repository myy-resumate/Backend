package dev.resumate.common.s3;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Operations s3Operations;


    /**
     * S3에 업로드
     * @param file
     * @param uploadKey
     * @return
     * @throws IOException
     */
    public S3Resource uploadObject(MultipartFile file, String uploadKey) throws IOException {
        InputStream inputStream = file.getInputStream();

        //파일 이름이 없는 경우
        if (file.getOriginalFilename() == null) {
            throw new BusinessBaseException(ErrorCode.FILE_NAME_IS_NULL);
        }

        Path path = Paths.get(file.getOriginalFilename());
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            System.out.println("뭐지");
            contentType = file.getContentType();
            System.out.println(contentType);
        }
        return s3Operations.upload(bucketName, uploadKey, inputStream, ObjectMetadata.builder().contentType(contentType).build());
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
