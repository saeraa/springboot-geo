package com.example.springrest.location;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> getWithin(@RequestParam Optional<Double> lat,
                                       @RequestParam Optional<Double> lon,
                                       @RequestParam Optional<Double> dist,
                                       @RequestBody(required = false) Optional<Coordinates[]> coordinates) {

        if (dist.isPresent() && lat.isPresent() && lon.isPresent()) {
            return ResponseEntity.ok().body(locationService.findAround(lat.get(), lon.get(), dist.get()));
        } else if (coordinates.isPresent()) {
            // this does not work properly, I have no idea why. Geometry does my head in
            return ResponseEntity.ok().body(locationService.findWithinPolygon(coordinates.get()));
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Optional<Long> search,
                                    @RequestParam(required = false) Optional<Long> category
    ) {
        if (search.isPresent()) {
            return ResponseEntity.ok().body(locationService.findPublicById(search.get()));
        } else if (category.isPresent()) {
            return ResponseEntity.ok().body(locationService.getPublicByCategory(category.get()));
        }
        return ResponseEntity.ok().body(locationService.getAllPublic());
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getSpecific(@PathVariable String userId) {
        return ResponseEntity.ok().body(locationService.getByUserId(userId));
    }

    @PostMapping
    @RolesAllowed("USER")
    public ResponseEntity<Location> createNew(@RequestBody LocationDto location) {
        var created = locationService.createNew(location);

        URI locationURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(locationURI).body(created);
    }

    @PatchMapping(path = "/{locationId}")
    public ResponseEntity<?> updateLocation(@PathVariable Long locationId, @RequestBody LocationDto location) {
        return ResponseEntity.ok().body(locationService.update(locationId, location));
    }

    @DeleteMapping(path = "/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long locationId) throws IllegalAccessException {
        locationService.delete(locationId);
        return ResponseEntity.noContent().build();
    }

}