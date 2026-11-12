package io.github.kosmaliczi.songover.catalog.artist.api;

import io.github.kosmaliczi.songover.catalog.artist.api.dto.ArtistPatchRequest;
import io.github.kosmaliczi.songover.catalog.artist.api.dto.ArtistRequest;
import io.github.kosmaliczi.songover.catalog.artist.api.dto.ArtistResponse;
import io.github.kosmaliczi.songover.catalog.artist.application.ArtistService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistResponse>> getArtists() {
        return ResponseEntity.ok(artistService.getArtists());
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable UUID artistId) {
        return ResponseEntity.ok(artistService.getArtistById(artistId));
    }

    @PostMapping
    public ResponseEntity<ArtistResponse> addArtist(@Valid @RequestBody ArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.addArtist(request));
    }

    @PatchMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> patchArtist(@PathVariable UUID artistId,
                                                      @Valid @RequestBody ArtistPatchRequest request) {
        return ResponseEntity.ok(artistService.patchArtistById(request, artistId));
    }
}
