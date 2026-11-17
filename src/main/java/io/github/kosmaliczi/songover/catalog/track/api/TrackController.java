package io.github.kosmaliczi.songover.catalog.track.api;

import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackAlbumRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;
import io.github.kosmaliczi.songover.catalog.track.application.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @GetMapping
    public ResponseEntity<List<TrackResponse>> getTracks(@RequestParam(required = false) UUID artistId,
                                                         @RequestParam(required = false) UUID albumId) {
        return ResponseEntity.ok(trackService.getTracks(artistId, albumId));
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackResponse> getTrackById(@PathVariable UUID trackId) {
        return ResponseEntity.ok(trackService.getTrackById(trackId));
    }

    @PostMapping
    public ResponseEntity<TrackResponse> addTrack(@Valid @RequestBody TrackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trackService.addTrack(request));
    }

    @PatchMapping("/{trackId}")
    public ResponseEntity<TrackResponse> patchTrack(@PathVariable UUID trackId,
                                                    @Valid @RequestBody TrackPatchRequest request) {
        return ResponseEntity.ok(trackService.patchTrackById(request, trackId));
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable UUID trackId) {
        trackService.deleteTrackById(trackId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{trackId}/publish")
    public ResponseEntity<TrackResponse> publishTrack(@PathVariable UUID trackId) {
        return ResponseEntity.ok(trackService.publishTrack(trackId));
    }

    @PostMapping("/{trackId}/unpublish")
    public ResponseEntity<TrackResponse> unpublishTrack(@PathVariable UUID trackId) {
        return ResponseEntity.ok(trackService.unpublishTrack(trackId));
    }

    @PutMapping("/{trackId}/album")
    public ResponseEntity<TrackResponse> assignTrackToAlbum(@PathVariable UUID trackId,
                                                            @Valid @RequestBody TrackAlbumRequest request) {
        return ResponseEntity.ok(trackService.assignTrackToAlbum(trackId, request));
    }

    @DeleteMapping("/{trackId}/album")
    public ResponseEntity<Void> removeTrackFromAlbum(@PathVariable UUID trackId) {
        trackService.removeTrackFromAlbum(trackId);
        return ResponseEntity.noContent().build();
    }
}