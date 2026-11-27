package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackNotFoundException;
import io.github.kosmaliczi.songover.catalog.track.domain.model.Track;
import io.github.kosmaliczi.songover.catalog.track.domain.repository.TrackRepository;
import io.github.kosmaliczi.songover.media.api.AudioProcessing;
import io.github.kosmaliczi.songover.media.events.UploadCompleted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class UploadCompletedListener {

    private final TrackRepository trackRepository;
    private final AudioProcessing audioProcessing;

    @ApplicationModuleListener
    void on(UploadCompleted event) {
        Track track = trackRepository.findById(event.trackId())
                .orElseThrow(() -> new TrackNotFoundException(event.trackId()));

        track.startProcessing();
        audioProcessing.requestProcessing(event.uploadId());

        log.info("Track {} moved to PROCESSING after upload {}", track.getId(), event.uploadId());
    }
}
