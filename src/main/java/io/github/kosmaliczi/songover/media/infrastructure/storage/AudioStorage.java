package io.github.kosmaliczi.songover.media.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AudioStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;

    public PresignedUpload createUploadUrl(String objectKey, String contentType, long sizeBytes) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(contentType)
                .contentLength(sizeBytes)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(request -> request
                .signatureDuration(properties.presignedUrlTtl())
                .putObjectRequest(objectRequest));

        return new PresignedUpload(presigned.url().toString(), presigned.expiration());
    }

    public String createDownloadUrl(String objectKey) {
        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(request -> request
                .signatureDuration(properties.presignedUrlTtl())
                .getObjectRequest(get -> get
                        .bucket(properties.bucket())
                        .key(objectKey)));

        return presigned.url().toString();
    }

    public Optional<Long> findObjectSize(String objectKey) {
        try {
            HeadObjectResponse response = s3Client.headObject(request -> request
                    .bucket(properties.bucket())
                    .key(objectKey));
            return Optional.of(response.contentLength());
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}