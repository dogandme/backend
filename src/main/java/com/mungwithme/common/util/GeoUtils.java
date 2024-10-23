package com.mungwithme.common.util;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.maps.dto.response.LocationBoundsDto;

public class GeoUtils {




    /**
     *     대한민국의 위도 및 경도 범위 확인
     * @param lat
     * @param lng
     * @return
     */
    public static void isWithinKorea(double lat, double lng) {
        // 대한민국의 위도 및 경도 범위 설정
        double minLat = 33.0;
        double maxLat = 43.0;
        double minLng = 124.0;
        double maxLng = 132.0;

        // 위도와 경도가 대한민국 범위 내에 있는지 확인
        boolean isKorea = lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;

        if (!isKorea){
            throw new ResourceNotFoundException("error.notfound.coordinates");
        }
    }

    public static void checkLocationBoundsDto(LocationBoundsDto locationBoundsDto) {
        GeoUtils.isWithinKorea(locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getNorthRightLng());
        GeoUtils.isWithinKorea(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getSouthLeftLng());
    }
}
