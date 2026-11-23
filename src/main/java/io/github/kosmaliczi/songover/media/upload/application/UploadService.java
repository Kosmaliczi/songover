package io.github.kosmaliczi.songover.media.upload.application;

import io.github.kosmaliczi.songover.media.upload.api.dto.UploadRequest;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadResponse;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadUrlResponse;

import java.util.UUID;

public interface UploadService {

    UploadUrlResponse createUpload(UUID trackId, UploadRequest request);

    UploadResponse completeUpload(UUID uploadId);

    UploadResponse getUploadById(UUID uploadId);
}