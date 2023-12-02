package com.example.springrest.location;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends ListCrudRepository<Location, Long> {
    List<Location> getLocationsByUserId(String userId);
    List<Location> findAllByCategory_Name(String category);

    @Query(value = """
            SELECT * FROM location
            WHERE ST_Within(coordinate, ST_GeomFromText(:polygon, 4326))
                """, nativeQuery = true)
    List<Location> filterWithinPolygon(@Param("polygon") String polygon);

    @Query(value = """
            SELECT * FROM location
            WHERE ST_Distance_Sphere(coordinate, :location) < :distance
                """, nativeQuery = true)
    List<Location> filterOnDistance(@Param("location") Point<G2D> location, @Param("distance") double distance);
}
