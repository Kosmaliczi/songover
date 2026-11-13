package io.github.kosmaliczi.songover.catalog.album.api;

import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumPatchRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumResponse;
import io.github.kosmaliczi.songover.catalog.album.application.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping
    public ResponseEntity<List<AlbumResponse>> getAlbums(@RequestParam(required = false) UUID artistId) {
        return ResponseEntity.ok(albumService.getAlbums(artistId));
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable UUID albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> addAlbum(@Valid @RequestBody AlbumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.addAlbum(request));
    }

    @PatchMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> patchAlbum(@PathVariable UUID albumId,
                                                    @Valid @RequestBody AlbumPatchRequest request) {
        return ResponseEntity.ok(albumService.patchAlbumById(request, albumId));
    }
}