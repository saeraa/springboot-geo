package com.example.springrest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmojiValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmojiConstraint {
    String message() default "Symbol must be a valid emoji";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
