package ru.netcracker.backend.exception.category;

import ru.netcracker.backend.exception.ValidationException;

public class CategoryNameAlreadyExistsException extends ValidationException {
    public CategoryNameAlreadyExistsException(String categoryName) {
        super(String.format("Category with name: %s already exists", categoryName));
    }
}
