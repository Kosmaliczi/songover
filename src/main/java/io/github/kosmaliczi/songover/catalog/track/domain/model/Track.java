package io.github.kosmaliczi.songover.catalog.track.domain.model;

import io.github.kosmaliczi.songover.catalog.album.domain.model.Album;
import io.github.kosmaliczi.songover.catalog.artist.domain.model.Artist;
import io.github.kosmaliczi.songover.catalog.track.domain.exception.InvalidTrackStatusTransitionException;
import io.github.kosmaliczi.songover.catalog.track.domain.exception.TrackAlbumArtistMismatchException;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    private Integer trackNumber;

    private Integer durationSeconds;

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

    public void publish() {
        changeStatus(TrackStatus.PUBLISHED);
    }

    public void unpublish() {
        changeStatus(TrackStatus.UNPUBLISHED);
    }

    public void startProcessing() {
        changeStatus(TrackStatus.PROCESSING);
    }

    public void markAsReady(int durationSeconds) {
        changeStatus(TrackStatus.READY);
        this.durationSeconds = durationSeconds;
    }

    public void markAsFailed() {
        changeStatus(TrackStatus.FAILED);
    }

    public boolean canStartProcessing() {
        return status.canTransitionTo(TrackStatus.PROCESSING);
    }

    public void assignToAlbum(Album album, int trackNumber) {
        if (!album.getArtist().getId().equals(artist.getId())) {
            throw new TrackAlbumArtistMismatchException(id, album.getId());
        }
        this.album = album;
        this.trackNumber = trackNumber;
    }

    public void removeFromAlbum() {
        this.album = null;
        this.trackNumber = null;
    }

    private void changeStatus(TrackStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidTrackStatusTransitionException(id, status, target);
        }
        this.status = target;
    }
}
