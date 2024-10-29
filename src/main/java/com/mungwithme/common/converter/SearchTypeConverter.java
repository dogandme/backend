package com.mungwithme.common.converter;


import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SearchType;
import org.springframework.core.convert.converter.Converter;

public class SearchTypeConverter implements Converter<String, SearchType> {
    @Override
    public SearchType convert(String requestCategory) {
        return SearchType.create(requestCategory.toUpperCase());
    }

}
