package ru.netcracker.backend.service;

import ru.netcracker.backend.model.responses.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
