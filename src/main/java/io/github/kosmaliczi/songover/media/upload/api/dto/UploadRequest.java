package io.github.kosmaliczi.songover.media.upload.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record UploadRequest(
        @NotBlank(message = "Content type is required")
        @Pattern(regexp = "audio/(wav|x-wav|flac|mpeg)", message = "Only WAV, FLAC and MP3 files are allowed")
        String contentType,

        @NotNull(message = "File size is required")
        @Positive(message = "File size must be greater than 0")
        @Max(value = 209_715_200, message = "File size must not exceed 200 MB")
        Long sizeBytes
) {
}