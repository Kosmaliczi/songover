package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.track.domain.repository.TrackRepository;
import io.github.kosmaliczi.songover.media.TrackUploadEligibility;
import io.github.kosmaliczi.songover.media.UploadEligibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class TrackUploadEligibilityAdapter implements TrackUploadEligibility {

    private final TrackRepository trackRepository;

    @Override
    @Transactional(readOnly = true)
    public UploadEligibility check(UUID trackId) {
        return trackRepository.findById(trackId)
                .map(track -> track.canStartProcessing()
                        ? UploadEligibility.ELIGIBLE
                        : UploadEligibility.TRACK_NOT_ACCEPTING_UPLOAD)
                .orElse(UploadEligibility.TRACK_NOT_FOUND);
    }
}
