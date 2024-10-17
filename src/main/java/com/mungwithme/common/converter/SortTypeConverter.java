package com.mungwithme.common.converter;


import com.mungwithme.marking.model.enums.SortType;
import org.springframework.core.convert.converter.Converter;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String requestCategory) {
        return SortType.create(requestCategory.toUpperCase());
    }

}
