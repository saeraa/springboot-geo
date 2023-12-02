package com.example.springrest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.function.IntPredicate;

public class EmojiValidator implements
        ConstraintValidator<EmojiConstraint, String> {

    @Override
    public void initialize(EmojiConstraint emojiConstraint) {
    }

    @Override
    public boolean isValid(String emojiString,
                           ConstraintValidatorContext cxt) {
        IntPredicate predicate1 = Character::isEmoji;
        IntPredicate predicate2 = Character::isEmojiComponent;
        IntPredicate combinedPredicate = predicate1.or(predicate2);
        return emojiString != null && emojiString.codePoints().allMatch(combinedPredicate);

    }

}