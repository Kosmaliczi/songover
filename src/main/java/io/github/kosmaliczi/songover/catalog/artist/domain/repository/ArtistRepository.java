package io.github.kosmaliczi.songover.catalog.artist.domain.repository;

import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    boolean existsByName(String name);
}
