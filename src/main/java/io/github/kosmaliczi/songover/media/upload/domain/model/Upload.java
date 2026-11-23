package io.github.kosmaliczi.songover.media.upload.domain.model;

import io.github.kosmaliczi.songover.media.upload.domain.exception.UploadAlreadyCompletedException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "upload", schema = "media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Upload {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false)
    private UUID trackId;

    @Column(nullable = false, unique = true, length = 500)
    private String objectKey;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UploadStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant uploadedAt;

    @Version
    private Long version;

    public Upload(UUID trackId, String contentType, long sizeBytes) {
        this.trackId = trackId;
        this.objectKey = "tracks/" + trackId + "/original/" + UUID.randomUUID();
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.status = UploadStatus.PENDING_UPLOAD;
        this.createdAt = Instant.now();
    }

    public void markAsUploaded() {
        if (status != UploadStatus.PENDING_UPLOAD) {
            throw new UploadAlreadyCompletedException(id);
        }
        this.status = UploadStatus.UPLOADED;
        this.uploadedAt = Instant.now();
    }
}