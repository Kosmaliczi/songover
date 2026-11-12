package io.github.kosmaliczi.songover.catalog.track.domain.repository;

import io.github.kosmaliczi.songover.catalog.track.domain.model.Track;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {

    @Override
    @EntityGraph(attributePaths = "artist")
    List<Track> findAll();

    @EntityGraph(attributePaths = "artist")
    List<Track> findAllByArtistId(UUID artistId);
}
