package io.github.kosmaliczi.songover.media.upload.application;

import io.github.kosmaliczi.songover.media.infrastructure.storage.AudioStorage;
import io.github.kosmaliczi.songover.media.infrastructure.storage.PresignedUpload;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadRequest;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadResponse;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadUrlResponse;
import io.github.kosmaliczi.songover.media.upload.api.mapper.UploadMapper;
import io.github.kosmaliczi.songover.media.upload.domain.exception.FileNotUploadedException;
import io.github.kosmaliczi.songover.media.upload.domain.exception.UploadNotFoundException;
import io.github.kosmaliczi.songover.media.upload.domain.model.Upload;
import io.github.kosmaliczi.songover.media.upload.domain.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UploadServiceImpl implements UploadService {

    private final UploadRepository uploadRepository;
    private final AudioStorage audioStorage;
    private final UploadMapper uploadMapper;

    @Override
    @Transactional
    public UploadUrlResponse createUpload(UUID trackId, UploadRequest request) {
        Upload upload = uploadRepository.save(new Upload(trackId, request.contentType(), request.sizeBytes()));
        PresignedUpload presigned = audioStorage.createUploadUrl(
                upload.getObjectKey(), upload.getContentType(), upload.getSizeBytes());
        return new UploadUrlResponse(upload.getId(), presigned.url(), presigned.expiresAt());
    }

    @Override
    @Transactional
    public UploadResponse completeUpload(UUID uploadId) {
        Upload upload = findUpload(uploadId);
        boolean fileMatches = audioStorage.findObjectSize(upload.getObjectKey())
                .filter(size -> size == upload.getSizeBytes())
                .isPresent();
        if (!fileMatches) {
            throw new FileNotUploadedException(uploadId);
        }
        upload.markAsUploaded();
        return uploadMapper.toResponse(upload);
    }

    @Override
    public UploadResponse getUploadById(UUID uploadId) {
        return uploadMapper.toResponse(findUpload(uploadId));
    }

    private Upload findUpload(UUID uploadId) {
        return uploadRepository.findById(uploadId)
                .orElseThrow(() -> new UploadNotFoundException(uploadId));
    }
}