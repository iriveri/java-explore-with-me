package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, List<Long>> {
    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null lists are considered valid. Use @NotNull for non-null validation.
        }
        Set<Long> uniqueElements = new HashSet<>(value);
        return uniqueElements.size() == value.size();
    }
}