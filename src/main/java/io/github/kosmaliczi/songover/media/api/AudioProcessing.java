package io.github.kosmaliczi.songover.media.api;

import java.util.UUID;

public interface AudioProcessing {

    void requestProcessing(UUID uploadId);
}
