package io.github.kosmaliczi.songover.catalog.application;

import io.github.kosmaliczi.songover.catalog.api.dto.ArtistPatchRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistResponse;
import io.github.kosmaliczi.songover.catalog.api.mapper.ArtistMapper;
import io.github.kosmaliczi.songover.catalog.domain.exception.ArtistAlreadyExistsException;
import io.github.kosmaliczi.songover.catalog.domain.exception.ArtistNotFoundException;
import io.github.kosmaliczi.songover.catalog.domain.model.Artist;
import io.github.kosmaliczi.songover.catalog.domain.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public List<ArtistResponse> getArtists() {
        return artistMapper.toResponseList(artistRepository.findAll());
    }

    @Override
    public ArtistResponse getArtistById(UUID artistId) {
        return artistMapper.toResponse(findArtist(artistId));
    }

    @Override
    @Transactional
    public ArtistResponse addArtist(ArtistRequest request) {
        if (artistRepository.existsByName(request.name())) {
            throw new ArtistAlreadyExistsException(request.name());
        }
        Artist artist = artistRepository.save(artistMapper.toEntity(request));
        return artistMapper.toResponse(artist);
    }

    @Override
    @Transactional
    public ArtistResponse patchArtistById(ArtistPatchRequest request, UUID artistId) {
        Artist artist = findArtist(artistId);
        if (isNameTaken(request.name(), artist)) {
            throw new ArtistAlreadyExistsException(request.name());
        }
        artistMapper.updateFromPatch(request, artist);
        return artistMapper.toResponse(artist);
    }

    private Artist findArtist(UUID artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
    }

    private boolean isNameTaken(String newName, Artist artist) {
        return newName != null
                && !newName.equals(artist.getName())
                && artistRepository.existsByName(newName);
    }
}
