package io.github.kosmaliczi.songover.catalog.track.api.mapper;

import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackPatchRequest;
import io.github.kosmaliczi.songover.catalog.track.api.dto.TrackResponse;
import io.github.kosmaliczi.songover.catalog.track.domain.model.Track;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackMapper {

    @Mapping(target = "artistId", source = "artist.id")
    @Mapping(target = "artistName", source = "artist.name")
    TrackResponse toResponse(Track track);

    List<TrackResponse> toResponseList(List<Track> tracks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromPatch(TrackPatchRequest request, @MappingTarget Track track);
}
