package io.github.kosmaliczi.songover.catalog.application;

import io.github.kosmaliczi.songover.catalog.api.dto.ArtistPatchRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistResponse;

import java.util.List;
import java.util.UUID;

public interface ArtistService {

    List<ArtistResponse> getArtists();

    ArtistResponse getArtistById(UUID artistId);

    ArtistResponse addArtist(ArtistRequest request);

    ArtistResponse patchArtistById(ArtistPatchRequest request, UUID artistId);
}
