package io.github.kosmaliczi.songover.catalog.album.application;

import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumPatchRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumResponse;
import io.github.kosmaliczi.songover.catalog.album.api.mapper.AlbumMapper;
import io.github.kosmaliczi.songover.catalog.album.domain.exception.AlbumNotFoundException;
import io.github.kosmaliczi.songover.catalog.album.domain.model.Album;
import io.github.kosmaliczi.songover.catalog.album.domain.repository.AlbumRepository;
import io.github.kosmaliczi.songover.catalog.artist.domain.exception.ArtistNotFoundException;
import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import io.github.kosmaliczi.songover.catalog.artist.domain.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumMapper albumMapper;

    @Override
    public List<AlbumResponse> getAlbums(UUID artistId) {
        List<Album> albums = artistId == null
                ? albumRepository.findAll()
                : albumRepository.findAllByArtistId(artistId);
        return albumMapper.toResponseList(albums);
    }

    @Override
    public AlbumResponse getAlbumById(UUID albumId) {
        return albumMapper.toResponse(findAlbum(albumId));
    }

    @Override
    @Transactional
    public AlbumResponse addAlbum(AlbumRequest request) {
        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new ArtistNotFoundException(request.artistId()));
        Album album = albumRepository.save(new Album(request.title(), request.releaseDate(), artist));
        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional
    public AlbumResponse patchAlbumById(AlbumPatchRequest request, UUID albumId) {
        Album album = findAlbum(albumId);
        albumMapper.updateFromPatch(request, album);
        return albumMapper.toResponse(album);
    }

    private Album findAlbum(UUID albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new AlbumNotFoundException(albumId));
    }
}