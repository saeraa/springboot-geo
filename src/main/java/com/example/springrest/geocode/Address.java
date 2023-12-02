package com.example.springrest.geocode;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        @JsonProperty("house_number")
        String houseNumber,
        String road,
        String village,
        String country,
        String postcode
) {
}
