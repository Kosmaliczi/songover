package io.github.kosmaliczi.songover.media.spi;

import java.util.UUID;

public interface TrackUploadEligibility {

    UploadEligibility check(UUID trackId);
}
