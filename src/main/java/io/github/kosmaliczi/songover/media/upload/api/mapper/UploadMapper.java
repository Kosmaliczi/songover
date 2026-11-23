package io.github.kosmaliczi.songover.media.upload.api.mapper;

import io.github.kosmaliczi.songover.media.upload.api.dto.UploadResponse;
import io.github.kosmaliczi.songover.media.upload.domain.model.Upload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadMapper {

    UploadResponse toResponse(Upload upload);
}