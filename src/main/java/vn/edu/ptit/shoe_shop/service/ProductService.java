package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.ProductCreationRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    public ApiResponse<ProductResponseDTO> createProduct(ProductCreationRequestDTO request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }
        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .build();
        Product savedProduct = productRepository.save(product);

        return ApiResponse.ok(ProductResponseDTO.builder()
                .productId(savedProduct.getProductId())
                .title(savedProduct.getTitle())
                .description(savedProduct.getDescription())
                .categoryId(savedProduct.getCategory() != null ? savedProduct.getCategory().getCategoryId() : null)
                .build(), "Product created successfully");
    }

    public ApiResponse<ProductResponseDTO> updateProduct(Integer id, ProductUpdateRequestDTO request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (request.getTitle() != null)
            product.setTitle(request.getTitle());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        productRepository.save(product);
        return ApiResponse.ok(ProductResponseDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .createdAt(product.getCreatedAt())
                .build(), "Product updated successfully");
    }

    public ApiResponse<Void> deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
        return ApiResponse.ok(null, "Product deleted successfully");
    }

    public ApiResponse<ProductResponseDTO> getProduct(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ApiResponse.ok(ProductResponseDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .createdAt(product.getCreatedAt())
                .build());
    }

    public ApiResponse<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> response = products.stream()
                .map(p -> ProductResponseDTO.builder()
                        .productId(p.getProductId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .categoryId(p.getCategory() != null ? p.getCategory().getCategoryId() : null)
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();
        return  ApiResponse.ok(response, "All products successfully");
    }
}
