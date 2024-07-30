package ru.practicum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, List<Integer>> {
    @Override
    public boolean isValid(List<Integer> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null lists are considered valid. Use @NotNull for non-null validation.
        }
        Set<Integer> uniqueElements = new HashSet<>(value);
        return uniqueElements.size() == value.size();
    }
}