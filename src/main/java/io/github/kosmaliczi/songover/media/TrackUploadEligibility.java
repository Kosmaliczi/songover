package io.github.kosmaliczi.songover.media;

import java.util.UUID;

public interface TrackUploadEligibility {

    UploadEligibility check(UUID trackId);
}
