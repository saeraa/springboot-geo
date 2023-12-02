package com.example.springrest.geocode;

public record GeoCode(
        float lat,
        float lon,
        Address address
) {

}
