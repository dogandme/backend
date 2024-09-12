package com.mungwithme.common.util;

public class GeoUtils {




    /**
     *     대한민국의 위도 및 경도 범위 확인
     * @param lat
     * @param lng
     * @return
     */
    public static boolean isWithinKorea(double lat, double lng) {
        // 대한민국의 위도 및 경도 범위 설정
        double minLat = 33.0;
        double maxLat = 43.0;
        double minLng = 124.0;
        double maxLng = 132.0;

        // 위도와 경도가 대한민국 범위 내에 있는지 확인
        return (lat >= minLat && lat <= maxLat) && (lng >= minLng && lng <= maxLng);
    }
}
