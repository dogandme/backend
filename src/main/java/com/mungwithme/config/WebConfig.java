package com.mungwithme.config;

import com.mungwithme.common.converter.MapViewModeConverter;
import com.mungwithme.common.converter.SortTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(new MapViewModeConverter());
        registry.addConverter(new SortTypeConverter());

    }
}
