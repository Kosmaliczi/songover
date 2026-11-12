package io.github.kosmaliczi.songover.catalog.api.mapper;

import io.github.kosmaliczi.songover.catalog.api.dto.ArtistPatchRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistRequest;
import io.github.kosmaliczi.songover.catalog.api.dto.ArtistResponse;
import io.github.kosmaliczi.songover.catalog.domain.model.Artist;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    Artist toEntity(ArtistRequest request);

    ArtistResponse toResponse(Artist artist);

    List<ArtistResponse> toResponseList(List<Artist> artists);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(ArtistPatchRequest request, @MappingTarget Artist artist);
}
