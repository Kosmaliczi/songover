package io.github.kosmaliczi.songover.media.upload.api;

import io.github.kosmaliczi.songover.media.upload.api.dto.UploadRequest;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadResponse;
import io.github.kosmaliczi.songover.media.upload.api.dto.UploadUrlResponse;
import io.github.kosmaliczi.songover.media.upload.application.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/tracks/{trackId}/uploads")
    public ResponseEntity<UploadUrlResponse> createUpload(@PathVariable UUID trackId,
                                                          @Valid @RequestBody UploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.createUpload(trackId, request));
    }

    @PostMapping("/uploads/{uploadId}/complete")
    public ResponseEntity<UploadResponse> completeUpload(@PathVariable UUID uploadId) {
        return ResponseEntity.ok(uploadService.completeUpload(uploadId));
    }

    @GetMapping("/uploads/{uploadId}")
    public ResponseEntity<UploadResponse> getUploadById(@PathVariable UUID uploadId) {
        return ResponseEntity.ok(uploadService.getUploadById(uploadId));
    }
}