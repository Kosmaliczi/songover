package io.github.kosmaliczi.songover.catalog.album.application;

import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumPatchRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumResponse;

import java.util.List;
import java.util.UUID;

public interface AlbumService {

    List<AlbumResponse> getAlbums(UUID artistId);

    AlbumResponse getAlbumById(UUID albumId);

    AlbumResponse addAlbum(AlbumRequest request);

    AlbumResponse patchAlbumById(AlbumPatchRequest request, UUID albumId);
}