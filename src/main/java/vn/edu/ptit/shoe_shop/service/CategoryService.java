package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.CategoryRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.CateProResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.exception.BusinessException;
import vn.edu.ptit.shoe_shop.exception.DuplicateResourceException;
import vn.edu.ptit.shoe_shop.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;

    public ApiResponse<CategoryResponseDTO> createCategory(CategoryRequestDTO request) {

        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException("Category name already exists");
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        Category savedCategory = categoryRepository.save(
                Category.builder()
                        .categoryName(request.getCategoryName())
                        .parent(parent)
                        .products(new HashSet<>())
                        .build()
        );
        List<CateProResponseDTO> products = null;
        if (request.getAddProductIds() != null) {
            Set<Integer> productIds = request.getAddProductIds();
            Set<Product> addProducts = productRepository.findAllByProductIdIn(productIds);
            if (addProducts.size() != productIds.size()) {
                throw new ResourceNotFoundException("One or more products not found");
            }
            addProducts.forEach(p -> p.setCategory(savedCategory));
            productRepository.saveAll(addProducts);
            products = addProducts.stream()
                    .map(p -> CateProResponseDTO.builder()
                            .productId(p.getProductId())
                            .createdAt(p.getCreatedAt())
                            .title(p.getTitle())
                            .build())
                    .toList();
        }

        return ApiResponse.ok(
                CategoryResponseDTO.builder()
                        .CategoryId(savedCategory.getCategoryId())
                        .CategoryName(savedCategory.getCategoryName())
                        .ParentId(
                                savedCategory.getParent() != null
                                        ? savedCategory.getParent().getCategoryId()
                                        : null
                        )
                        .productCount(products != null ? products.size() : 0)
                        .products(products)
                        .build(),
                "Create category successfully"
        );
    }

    public ApiResponse<CategoryResponseDTO> updateCategory(Integer id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (
                categoryRepository.existsByCategoryName(request.getCategoryName())
                        && !category.getCategoryName().equals(request.getCategoryName())
        ) {
            throw new DuplicateResourceException("Category name already exists");
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

        if (request.getAddProductIds() != null) {
            Set<Integer> productIds = request.getAddProductIds();
            Set<Product> addProducts = productRepository.findAllByProductIdIn(productIds);
            if (addProducts.size() != productIds.size()) {
                throw new ResourceNotFoundException("One or more products not found");
            }
            addProducts.forEach(p -> p.setCategory(category));
        }
        if (request.getRemoveProductIds() != null) {
            Set<Integer> productIds = request.getRemoveProductIds();
            Set<Product> removeProducts = productRepository.findAllByProductIdIn(productIds);
            if (removeProducts.size() != productIds.size()) {
                throw new ResourceNotFoundException("One or more products not found");
            }
            removeProducts.stream()
                    .filter(p -> Objects.equals(p.getCategory().getCategoryId(), id))
                    .forEach(p -> p.setCategory(null));
        }

        List<Product> products = productRepository.findAllByCategory(category);
        List<CateProResponseDTO> response = products.stream()
                .map(p -> CateProResponseDTO.builder()
                        .productId(p.getProductId())
                        .createdAt(p.getCreatedAt())
                        .title(p.getTitle())
                        .build())
                .toList();
        category.setCategoryName(request.getCategoryName());
        Category updatedCategory = categoryRepository.save(category);
        return ApiResponse.ok(CategoryResponseDTO.builder()
                        .CategoryId(updatedCategory.getCategoryId())
                        .CategoryName(updatedCategory.getCategoryName())
                        .ParentId(updatedCategory.getParent() != null ? category.getParent().getCategoryId() : null)
                        .productCount(response.size())
                        .products(response)
                        .build(),
                "Update category successfully");
    }

    public ApiResponse<Void> deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        List<Product> products = productRepository.findAllByCategory(category);
        products.forEach(p -> p.setCategory(null));
        categoryRepository.delete(category);
        return ApiResponse.ok(null, "Delete category successfully");
    }

    public ApiResponse<CategoryResponseDTO> getCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        List<Product> products = productRepository.findAllByCategory(category);
        List<CateProResponseDTO> response = products.stream()
                .map(p -> CateProResponseDTO.builder()
                        .productId(p.getProductId())
                        .createdAt(p.getCreatedAt())
                        .title(p.getTitle())
                        .build())
                .toList();
        return ApiResponse.ok(CategoryResponseDTO.builder()
                .CategoryId(category.getCategoryId())
                .CategoryName(category.getCategoryName())
                .ParentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .productCount(response.size())
                .products(response)
                .build());
    }

    public ApiResponse<List<CategoryResponseDTO>> getAll() {
        List<Category> all = categoryRepository.findAll();
        List<CategoryResponseDTO> response = all.stream()
                .map(p -> CategoryResponseDTO.builder()
                        .CategoryId(p.getCategoryId())
                        .CategoryName(p.getCategoryName())
                        .ParentId(p.getParent() != null ? p.getParent().getCategoryId() : null)
                        .build())
                .toList();
        return ApiResponse.ok(response, "Get all categories successfully");
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
