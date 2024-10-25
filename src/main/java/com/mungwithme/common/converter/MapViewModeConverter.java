package com.mungwithme.common.converter;


import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import org.springframework.core.convert.converter.Converter;

public class MapViewModeConverter implements Converter<String, MapViewMode> {
    @Override
    public MapViewMode convert(String requestCategory) {
        return MapViewMode.create(requestCategory.toUpperCase());
    }

}
