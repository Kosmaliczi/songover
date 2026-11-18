package io.github.kosmaliczi.songover.media.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Slf4j
@Component
@RequiredArgsConstructor
class BucketInitializer implements ApplicationRunner {

    private final S3Client s3Client;
    private final StorageProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        String bucket = properties.bucket();
        try {
            s3Client.headBucket(request -> request.bucket(bucket));
            log.info("Storage bucket '{}' is available", bucket);
        } catch (NoSuchBucketException ex) {
            s3Client.createBucket(request -> request.bucket(bucket));
            log.info("Storage bucket '{}' created", bucket);
        }
    }
}