package io.github.kosmaliczi.songover.catalog.album.api.mapper;

import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumPatchRequest;
import io.github.kosmaliczi.songover.catalog.album.api.dto.AlbumResponse;
import io.github.kosmaliczi.songover.catalog.album.domain.model.Album;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "artistId", source = "artist.id")
    @Mapping(target = "artistName", source = "artist.name")
    AlbumResponse toResponse(Album album);

    List<AlbumResponse> toResponseList(List<Album> albums);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(AlbumPatchRequest request, @MappingTarget Album album);
}