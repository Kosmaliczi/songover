package io.github.kosmaliczi.songover.catalog.album.domain.model;

import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "album", schema = "catalog")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Setter
    @Column(nullable = false, length = 200)
    private String title;

    @Setter
    private LocalDate releaseDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Album(String title, LocalDate releaseDate, Artist artist) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.artist = artist;
        this.createdAt = Instant.now();
    }
}