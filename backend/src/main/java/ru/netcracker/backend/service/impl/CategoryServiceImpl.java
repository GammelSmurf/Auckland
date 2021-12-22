package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Category;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.repository.CategoryRepository;
import ru.netcracker.backend.service.CategoryService;
import ru.netcracker.backend.util.CategoryUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final CategoryUtil categoryUtil;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, CategoryUtil categoryUtil) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.categoryUtil = categoryUtil;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository
                .findAll().stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse addCategory(String categoryName) {
        categoryUtil.validateBeforeAdding(categoryName);
        return modelMapper.map(categoryRepository.save(new Category(categoryName)), CategoryResponse.class);
    }
}
