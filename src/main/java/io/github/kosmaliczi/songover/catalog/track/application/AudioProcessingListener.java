package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackNotFoundException;
import io.github.kosmaliczi.songover.catalog.track.domain.model.Track;
import io.github.kosmaliczi.songover.catalog.track.domain.repository.TrackRepository;
import io.github.kosmaliczi.songover.media.events.AudioProcessed;
import io.github.kosmaliczi.songover.media.events.AudioProcessingFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
class AudioProcessingListener {

    private final TrackRepository trackRepository;

    @ApplicationModuleListener
    void on(AudioProcessed event) {
        Track track = findTrack(event.trackId());
        track.markAsReady(event.durationSeconds());

        log.info("Track {} is READY, duration {} s", track.getId(), event.durationSeconds());
    }

    @ApplicationModuleListener
    void on(AudioProcessingFailed event) {
        Track track = findTrack(event.trackId());
        track.markAsFailed();

        log.warn("Track {} FAILED processing: {}", track.getId(), event.reason());
    }

    private Track findTrack(UUID trackId) {
        return trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId));
    }
}
