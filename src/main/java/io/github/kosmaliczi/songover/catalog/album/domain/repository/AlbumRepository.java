package io.github.kosmaliczi.songover.catalog.album.domain.repository;

import io.github.kosmaliczi.songover.catalog.album.domain.model.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Override
    @EntityGraph(attributePaths = "artist")
    List<Album> findAll();

    @EntityGraph(attributePaths = "artist")
    List<Album> findAllByArtistId(UUID artistId);
}