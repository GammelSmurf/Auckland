package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin("*")
@Slf4j
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
