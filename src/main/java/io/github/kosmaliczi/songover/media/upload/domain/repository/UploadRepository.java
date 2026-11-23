package io.github.kosmaliczi.songover.media.upload.domain.repository;

import io.github.kosmaliczi.songover.media.upload.domain.model.Upload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadRepository extends JpaRepository<Upload, UUID> {
}