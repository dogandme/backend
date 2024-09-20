package com.mungwithme.maps.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCandidate {
    private String formattedAddress;
    private String name;
    private Geometry geometry;

    // 기본 생성자
    public PlaceCandidate() {}

    // Getter와 Setter
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "PlaceCandidate{" +
            "formattedAddress='" + formattedAddress + '\'' +
            ", name='" + name + '\'' +
            ", geometry=" + geometry +
            '}';
    }
}
