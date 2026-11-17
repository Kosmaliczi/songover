package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.album.domain.exception.AlbumNotFoundException;
import io.github.kosmaliczi.songover.catalog.album.domain.model.Album;
import io.github.kosmaliczi.songover.catalog.album.domain.repository.AlbumRepository;
import io.github.kosmaliczi.songover.catalog.artist.domain.exception.ArtistNotFoundException;
import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import io.github.kosmaliczi.songover.catalog.artist.domain.repository.ArtistRepository;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackAlbumRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;
import io.github.kosmaliczi.songover.catalog.track.api.mapper.TrackMapper;
import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackNotFoundException;
import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackNumberTakenException;
import io.github.kosmaliczi.songover.catalog.track.domain.model.Track;
import io.github.kosmaliczi.songover.catalog.track.domain.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackMapper trackMapper;

    @Override
    public List<TrackResponse> getTracks(UUID artistId, UUID albumId) {
        List<Track> tracks;
        if (albumId != null) {
            tracks = trackRepository.findAllByAlbumIdOrderByTrackNumberAsc(albumId);
        } else if (artistId != null) {
            tracks = trackRepository.findAllByArtistId(artistId);
        } else {
            tracks = trackRepository.findAll();
        }
        return trackMapper.toResponseList(tracks);
    }

    @Override
    public TrackResponse getTrackById(UUID trackId) {
        return trackMapper.toResponse(findTrack(trackId));
    }

    @Override
    @Transactional
    public TrackResponse addTrack(TrackRequest request) {
        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new ArtistNotFoundException(request.artistId()));
        Track track = trackRepository.save(new Track(request.title(), request.explicit(), artist));
        return trackMapper.toResponse(track);
    }

    @Override
    @Transactional
    public TrackResponse patchTrackById(TrackPatchRequest request, UUID trackId) {
        Track track = findTrack(trackId);
        trackMapper.updateFromPatch(request, track);
        return trackMapper.toResponse(track);
    }

    @Override
    @Transactional
    public void deleteTrackById(UUID trackId) {
        if (!trackRepository.existsById(trackId)) {
            throw new TrackNotFoundException(trackId);
        }
        trackRepository.deleteById(trackId);
    }

    @Override
    @Transactional
    public TrackResponse publishTrack(UUID trackId) {
        Track track = findTrack(trackId);
        track.publish();
        return trackMapper.toResponse(track);
    }

    @Override
    @Transactional
    public TrackResponse unpublishTrack(UUID trackId) {
        Track track = findTrack(trackId);
        track.unpublish();
        return trackMapper.toResponse(track);
    }

    @Override
    @Transactional
    public TrackResponse assignTrackToAlbum(UUID trackId, TrackAlbumRequest request) {
        Track track = findTrack(trackId);
        Album album = albumRepository.findById(request.albumId())
                .orElseThrow(() -> new AlbumNotFoundException(request.albumId()));
        if (trackRepository.existsByAlbumIdAndTrackNumberAndIdNot(album.getId(), request.trackNumber(), trackId)) {
            throw new TrackNumberTakenException(album.getId(), request.trackNumber());
        }
        track.assignToAlbum(album, request.trackNumber());
        return trackMapper.toResponse(track);
    }

    @Override
    @Transactional
    public void removeTrackFromAlbum(UUID trackId) {
        findTrack(trackId).removeFromAlbum();
    }

    private Track findTrack(UUID trackId) {
        return trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId));
    }
}