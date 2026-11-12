package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;

import java.util.List;
import java.util.UUID;

public interface TrackService {

    List<TrackResponse> getTracks(UUID artistId);

    TrackResponse getTrackById(UUID trackId);

    TrackResponse addTrack(TrackRequest request);

    TrackResponse patchTrackById(TrackPatchRequest request, UUID trackId);
}
