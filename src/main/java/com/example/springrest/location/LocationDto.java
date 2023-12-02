package com.example.springrest.location;

import java.io.Serializable;

public record LocationDto(String name,
                          String userId,
                          boolean privateTrue,
                          Long categoryId,
                          String description,
                          Coordinates coordinate) implements Serializable {
}
