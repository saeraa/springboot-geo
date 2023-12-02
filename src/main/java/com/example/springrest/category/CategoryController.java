package com.example.springrest.category;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> get() {
        return categoryService.findAll();
    }

    @PostMapping
    @RolesAllowed("ADMIN")
    public ResponseEntity<Category> create(@RequestBody CategoryDto category) {
        var createdCategory = categoryService.createNew(category);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(createdCategory.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdCategory);
    }

    @GetMapping("/{id}")
    public Optional<Category> getThing(@PathVariable Long id) {
        return categoryService.findById(id);
    }
}
