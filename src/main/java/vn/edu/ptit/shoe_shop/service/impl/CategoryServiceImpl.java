package vn.edu.ptit.shoe_shop.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.CategoryCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.CategoryUpdateRequestDTO;

import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.common.exception.BusinessException;
import vn.edu.ptit.shoe_shop.common.exception.DuplicateResourceException;
import vn.edu.ptit.shoe_shop.common.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;
import vn.edu.ptit.shoe_shop.service.CategoryService;

import java.util.*;
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    @CachePut(value = "categories", key = "#result.categoryId", unless = "#result == null")
    public CategoryResponseDTO create(CategoryCreateRequestDTO request) {

        Category parent = null;
        if (request.getParentId() != null) {

            if (!categoryRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Parent category not found");
            }

            parent = categoryRepository.findById(request.getParentId()).orElse(null);
        }

        if (categoryRepository.existsByCategoryNameAndParent(request.getCategoryName(), parent)) {
            throw new DuplicateResourceException("Category name already exists in this parent");
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
    @CachePut(value = "categories", key = "#id", unless = "#result == null")
    public CategoryResponseDTO update(UUID id, CategoryUpdateRequestDTO request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Category finalParent = category.getParent();
        String finalName = category.getCategoryName();

        // Nếu user muốn đổi parent
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Category cannot be its own parent");
            }

            finalParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

            validateNoCycle(category, finalParent);
        }


        if (request.getCategoryName() != null) {
            finalName = request.getCategoryName().trim();
        }

        // Check duplicate trong cùng parent
        if (categoryRepository.existsByCategoryNameAndParentAndCategoryIdNot(finalName, finalParent, id)) {
            throw new DuplicateResourceException("Category name already exists in this parent");
        }

        // Set các giá trị vào entity
        category.setCategoryName(finalName);
        category.setParent(finalParent);

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        // Save category
        Category updated = categoryRepository.save(category);
        // Trả về DTO
        return toResponse(updated);
        }

    @CacheEvict(value = "categories", key = "#id") // chỉ clear category này
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        boolean hasChildren = categoryRepository.existsByParent(category);
        if (hasChildren) {
            throw new BusinessException("Cannot delete category because it has child categories");
        }
        categoryRepository.delete(category);
    }

    @Cacheable(value = "categories", key = "#id")
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
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
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
