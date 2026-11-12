package io.github.kosmaliczi.songover.catalog.track.application;

import io.github.kosmaliczi.songover.catalog.artist.domain.exception.ArtistNotFoundException;
import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import io.github.kosmaliczi.songover.catalog.artist.domain.repository.ArtistRepository;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;
import io.github.kosmaliczi.songover.catalog.track.api.mapper.TrackMapper;
import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackNotFoundException;
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
    private final TrackMapper trackMapper;

    @Override
    public List<TrackResponse> getTracks(UUID artistId) {
        List<Track> tracks = artistId == null
                ? trackRepository.findAll()
                : trackRepository.findAllByArtistId(artistId);
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

    private Track findTrack(UUID trackId) {
        return trackRepository.findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException(trackId));
    }
}
