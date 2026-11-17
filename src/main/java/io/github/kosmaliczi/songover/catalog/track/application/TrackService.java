package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackAlbumRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;

import java.util.List;
import java.util.UUID;

public interface TrackService {

    List<TrackResponse> getTracks(UUID artistId, UUID albumId);

    TrackResponse getTrackById(UUID trackId);

    TrackResponse addTrack(TrackRequest request);

    TrackResponse patchTrackById(TrackPatchRequest request, UUID trackId);

    void deleteTrackById(UUID trackId);

    TrackResponse publishTrack(UUID trackId);

    TrackResponse unpublishTrack(UUID trackId);

    TrackResponse assignTrackToAlbum(UUID trackId, TrackAlbumRequest request);

    void removeTrackFromAlbum(UUID trackId);
}