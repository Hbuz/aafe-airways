package com.aafe.fareengine.mapper;

import com.aafe.fareengine.dto.airport.LongitudeResponse;
import com.aafe.fareengine.model.Longitude;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LongitudeMapper {
    Longitude longitudeDtoToLongitude(LongitudeResponse dto);
}
