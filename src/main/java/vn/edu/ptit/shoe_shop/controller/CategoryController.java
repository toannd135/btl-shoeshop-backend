package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.request.CategoryCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.CategoryUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;
import vn.edu.ptit.shoe_shop.service.CategoryService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    @ApiMessage("Category created successfully")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestBody @Valid CategoryCreateRequestDTO request) {

        CategoryResponseDTO res = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/{id}")
    @ApiMessage("Category updated successfully")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @RequestBody @Valid CategoryUpdateRequestDTO request,
            @PathVariable UUID id) {

        CategoryResponseDTO res = categoryService.update(id, request);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Category deleted successfully")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("Category retrieved successfully")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @PathVariable UUID id) {

        CategoryResponseDTO res = categoryService.getById(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping()
    @ApiMessage("Categories retrieved successfully")
    public ResponseEntity<List<CategoryResponseDTO>> fetchAllCategories() {
        List<CategoryResponseDTO> res = categoryService.getAll();
        return ResponseEntity.ok().body(res);
    }
}
