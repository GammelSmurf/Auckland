package ru.netcracker.backend.exception.category;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(Long categoryId) {
        super(String.format("Category with id: %d was not found", categoryId));
    }
}
