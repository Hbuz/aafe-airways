package com.aafe.fareengine.mapper;

import com.aafe.fareengine.dto.airport.AirportLocationResponse;
import com.aafe.fareengine.model.Coordinates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {LatitudeMapper.class, LongitudeMapper.class})
public interface AirportMapper {

    @Mapping(target = "latitude", source = "coordinates.latitude")
    @Mapping(target = "longitude", source = "coordinates.longitude")
    Coordinates locationDtoToCoordinates(AirportLocationResponse location);
}
