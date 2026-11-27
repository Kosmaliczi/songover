package io.github.kosmaliczi.songover.media.processing.application;

import io.github.kosmaliczi.songover.media.api.AudioProcessing;
import io.github.kosmaliczi.songover.media.events.AudioProcessed;
import io.github.kosmaliczi.songover.media.events.AudioProcessingFailed;
import io.github.kosmaliczi.songover.media.infrastructure.audio.AudioMetadata;
import io.github.kosmaliczi.songover.media.infrastructure.audio.AudioProber;
import io.github.kosmaliczi.songover.media.infrastructure.audio.InvalidAudioFileException;
import io.github.kosmaliczi.songover.media.infrastructure.storage.AudioStorage;
import io.github.kosmaliczi.songover.media.upload.domain.exception.UploadNotFoundException;
import io.github.kosmaliczi.songover.media.upload.domain.model.Upload;
import io.github.kosmaliczi.songover.media.upload.domain.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class AudioProcessingService implements AudioProcessing {

    private final UploadRepository uploadRepository;
    private final AudioStorage audioStorage;
    private final AudioProber audioProber;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void requestProcessing(UUID uploadId) {
        eventPublisher.publishEvent(new ProcessingRequested(uploadId));
    }

    @ApplicationModuleListener
    void on(ProcessingRequested event) {
        Upload upload = uploadRepository.findById(event.uploadId())
                .orElseThrow(() -> new UploadNotFoundException(event.uploadId()));

        String downloadUrl = audioStorage.createDownloadUrl(upload.getObjectKey());

        try {
            AudioMetadata metadata = audioProber.probe(downloadUrl);
            log.info("Upload {} is valid audio: {} / {}, {} s",
                    upload.getId(), metadata.formatName(), metadata.codecName(), metadata.durationSeconds());
            eventPublisher.publishEvent(
                    new AudioProcessed(upload.getId(), upload.getTrackId(), metadata.durationSeconds()));
        } catch (InvalidAudioFileException ex) {
            log.warn("Upload {} is not valid audio: {}", upload.getId(), ex.getMessage());
            eventPublisher.publishEvent(
                    new AudioProcessingFailed(upload.getId(), upload.getTrackId(), ex.getMessage()));
        }
    }
}
