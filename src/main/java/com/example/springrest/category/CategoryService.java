package com.example.springrest.category;

import com.example.springrest.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findCategoryByName(name);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category createNew(CategoryDto category) {
        var categoryCheck = categoryRepository.findCategoryByName(category.name());
        if (!categoryCheck.isPresent()) {
            return save(category);
        }
        throw new IllegalArgumentException("Category with the name " + category.name() + " already exist.");
    }

    public Category save(CategoryDto category) {
        Category categoryEntity = new Category();
        categoryEntity.setName(category.name());
        categoryEntity.setSymbol(category.symbol());
        return categoryRepository.save(categoryEntity);
    }

    public Optional<Category> findById(Long id) {
        var result = categoryRepository.findById(id);
        if (result.isPresent()) {
            return result;
        } else {
            throw new ResourceNotFoundException("Category with the id " + id.toString() + " was not found.");
        }
    }
}
