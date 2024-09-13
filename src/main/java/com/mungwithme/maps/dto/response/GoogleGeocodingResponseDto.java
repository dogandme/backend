package com.mungwithme.maps.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GoogleGeocodingResponseDto {

    @JsonProperty("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
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