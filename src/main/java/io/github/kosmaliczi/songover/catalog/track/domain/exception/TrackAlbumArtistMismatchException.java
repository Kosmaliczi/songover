package io.github.kosmaliczi.songover.catalog.track.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TrackAlbumArtistMismatchException extends BaseBusinessException {

    public TrackAlbumArtistMismatchException(UUID trackId, UUID albumId) {
        super("Track " + trackId + " and album " + albumId + " belong to different artists", HttpStatus.CONFLICT);
    }
}