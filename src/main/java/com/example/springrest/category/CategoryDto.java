package com.example.springrest.category;

import com.example.springrest.validation.EmojiConstraint;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CategoryDto(Long id,
                          String name,
                          @EmojiConstraint String symbol,
                          LocalDateTime created,
                          LocalDateTime modified)
        implements Serializable {
}
