package io.github.kosmaliczi.songover.catalog.track.domain.model;

import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "track", schema = "catalog")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Track {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Setter
    @Column(nullable = false, length = 200)
    private String title;

    @Setter
    @Column(nullable = false)
    private boolean explicit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrackStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    public Track(String title, boolean explicit, Artist artist) {
        this.title = title;
        this.explicit = explicit;
        this.artist = artist;
        this.status = TrackStatus.DRAFT;
        this.createdAt = Instant.now();
    }
}
