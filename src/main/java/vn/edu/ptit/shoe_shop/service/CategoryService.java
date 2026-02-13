package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.CategoryCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.CategoryUpdateRequestDTO;

import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.exception.BusinessException;
import vn.edu.ptit.shoe_shop.exception.DuplicateResourceException;
import vn.edu.ptit.shoe_shop.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;

import java.util.*;
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

    public CategoryResponseDTO create(CategoryCreateRequestDTO request) {

        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException("Category name already exists");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .parent(parent)
                .build();

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }

    public CategoryResponseDTO update(UUID id, CategoryUpdateRequestDTO request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.getCategoryName() != null &&
                categoryRepository.existsByCategoryName(request.getCategoryName()) &&
                !category.getCategoryName().equals(request.getCategoryName())) {
            throw new DuplicateResourceException("Category name already exists");
        }

        if (request.getCategoryName() != null) {
            category.setCategoryName(request.getCategoryName());
        }

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

            validateNoCycle(category, parent);
            category.setParent(parent);
        }

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    public CategoryResponseDTO getById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toResponse(category);
    }

    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoryResponseDTO toResponse(Category category) {
        return CategoryResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentId(
                        category.getParent() != null
                                ? category.getParent().getCategoryId()
                                : null
                )

                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private void validateNoCycle(Category category, Category newParent) {
        Category current = newParent;
        while (current != null) {
            if (current.getCategoryId().equals(category.getCategoryId())) {
                throw new BusinessException("Cyclic category hierarchy is not allowed");
            }
            current = current.getParent();
        }
    }
}
