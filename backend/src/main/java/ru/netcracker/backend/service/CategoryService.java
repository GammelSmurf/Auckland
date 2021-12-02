package ru.netcracker.backend.service;

import ru.netcracker.backend.responses.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
