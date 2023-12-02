package com.example.springrest.geocode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeoCodeController {

    GeoCodeService geoCodeService;

    public GeoCodeController(GeoCodeService geoCodeService) {
        this.geoCodeService = geoCodeService;
    }

    @GetMapping("/api/geo")
    GeoCode lookup(@RequestParam float lat, @RequestParam float lon) {
        return geoCodeService.reverseGeoCode(lat, lon);
    }
}