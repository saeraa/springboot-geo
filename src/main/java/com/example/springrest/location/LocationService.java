package com.example.springrest.location;

import com.example.springrest.category.Category;
import com.example.springrest.category.CategoryRepository;
import com.example.springrest.exception.ResourceNotFoundException;
//import org.geolatte.geom.*;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Point;
import org.geolatte.geom.builder.DSL;
import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public LocationService(LocationRepository locationRepository,
                           CategoryRepository categoryRepository) {
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    @PreAuthorize("#userId.equalsIgnoreCase(authentication.name)")
    public List<Location> getByUserId(String userId) {
        return locationRepository.getLocationsByUserId(userId);
    }

    public Location createNew(LocationDto location) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        var locationEntity = verify(location.coordinate());

        var category = categoryRepository.findById(location.categoryId());

        if (category.isEmpty())
            throw new IllegalArgumentException("No such category");

        locationEntity.setCategory(category.get());
        locationEntity.setName(location.name());
        locationEntity.setDescription(location.description());
        locationEntity.setPrivateTrue(location.privateTrue());
        locationEntity.setUserId(username);

        return locationRepository.save(locationEntity);
    }

    public Location verify(Coordinates location) {
        var locationEntity = new Location();

        if (location.lat() < -90 || location.lat() > 90 || location.lon() < -180 || location.lon() > 180) {
            throw new IllegalArgumentException("Invalid latitude or longitude");
        }
        var geo = Geometries.mkPoint(new G2D(location.lon(), location.lat()), WGS84);

        locationEntity.setCoordinate(geo);
        return locationEntity;
    }

    public Optional<Location> findById(Long locationId) {
        return locationRepository.findById(locationId);
    }

    @PostAuthorize("returnObject.get().userId.equalsIgnoreCase(authentication.name) || returnObject.get().privateTrue == false")
    public Optional<Location> findPublicById(Long locationId) {
        return locationRepository.findById(locationId);
    }

    @PostFilter("filterObject.privateTrue == false || filterObject.userId == authentication.name")
    public List<Location> getAllPublic() {
        return locationRepository.findAll();
    }

    @PreAuthorize("#location.userId().equalsIgnoreCase(authentication.name)")
    public Location update(Long locationId, LocationDto location) {

        Optional<Location> locationData = findById(locationId);

        if (locationData.isPresent()) {
            Location locationToUpdate = locationData.get();

            Optional<Category> category = categoryRepository.findById(location.categoryId());
            locationToUpdate.setCategory(category.orElseThrow(() -> new ResourceNotFoundException("Category not found")));
            locationToUpdate.setName(location.name());
            locationToUpdate.setDescription(location.description());

            return save(locationToUpdate);
        } else {
            throw new ResourceNotFoundException("Location with the id: " + locationId + " was not found");
        }
    }

    @PostFilter("filterObject.privateTrue == false || filterObject.userId == authentication.name")
    public List<Location> getPublicByCategory(Long categoryId) {
        var category = categoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found.");
        }

        return locationRepository.findAllByCategory_Name(category.get().getName());

    }

    // how to use @PreAuthorize here?
    public void delete(Long locationId) throws IllegalAccessException {
        var locationToDelete = locationRepository.findById(locationId);
        if (locationToDelete.isEmpty()) {
            throw new ResourceNotFoundException("Location with the ID: " + locationId + " was not found.");
        }

        var locationUserId = locationToDelete.get().getUserId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        if (locationUserId.equalsIgnoreCase(currentUser)) {
            locationRepository.deleteById(locationId);
        } else {
            throw new IllegalAccessException("You are not allowed to delete another user's entries.");
        }
    }

    public List<Location> findAround(double lat, double lon, double distance) {
        Point<G2D> location = DSL.point(WGS84, g(lon, lat));
        return locationRepository.filterOnDistance(location, distance);
    }

    public List<Location> findWithinPolygon(Coordinates[] coordinates) {
        if (coordinates == null || coordinates.length != 4) {
            throw new IllegalArgumentException("For filtering within an area, four coordinate points are required.");
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] polygonCoordinates = new Coordinate[coordinates.length + 1];
        for (int i = 0; i < coordinates.length; i++) {
            polygonCoordinates[i] = new Coordinate(coordinates[i].lon(), coordinates[i].lat());
        }
        polygonCoordinates[coordinates.length] = polygonCoordinates[0];
        Polygon polygon = geometryFactory.createPolygon(polygonCoordinates);

        polygon.setSRID(4326);

        return locationRepository.filterWithinPolygon(String.valueOf(polygon));
    }
}



