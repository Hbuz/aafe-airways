package com.aafe.fareengine.mapper;

import com.aafe.fareengine.dto.airport.LatitudeResponse;
import com.aafe.fareengine.model.Latitude;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LatitudeMapper {
    Latitude latitudeDtoToLatitude(LatitudeResponse dto);
}
