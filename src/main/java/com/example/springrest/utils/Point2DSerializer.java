package com.example.springrest.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

import java.io.IOException;

public class Point2DSerializer extends JsonSerializer<Point<G2D>> {
    @Override
    public void serialize(Point<G2D> value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("lat", value.getPosition().getCoordinate(1));
        gen.writeNumberField("lon", value.getPosition().getCoordinate(0));
        gen.writeEndObject();
    }
}