package ru.netcracker.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.category.CategoryNameAlreadyExistsException;
import ru.netcracker.backend.repository.CategoryRepository;

@Component
public class CategoryUtil {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryUtil(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateBeforeAdding(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new CategoryNameAlreadyExistsException(categoryName);
        }
    }
}
