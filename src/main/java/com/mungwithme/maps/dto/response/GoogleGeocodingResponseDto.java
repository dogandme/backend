package com.mungwithme.maps.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Setter;

@Setter(value = AccessLevel.PRIVATE)
public class GoogleGeocodingResponseDto {

    @JsonProperty("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        @JsonProperty("formatted_address")
        private String formattedAddress;

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }
    }
}