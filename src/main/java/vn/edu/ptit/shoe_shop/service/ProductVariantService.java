package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantCreationRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;

    public ApiResponse<ProductVariantResponseDTO> getProductVariant(Integer productId, Integer id) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        ;
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant not found with id: " + id));

        return ApiResponse.ok(ProductVariantResponseDTO.builder()
                .productVariantId(variant.getProductVariantId())
                .productId(product.getProductId())
                .color(variant.getColor())
                .size(variant.getSize())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .build());
    }

    public ApiResponse<List<ProductVariantResponseDTO>> getAllProductVariant(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        List<ProductVariant> variants = productVariantRepository.findByProduct(product);
        List<ProductVariantResponseDTO> response = variants.stream()
                .map(v -> ProductVariantResponseDTO.builder()
                        .productVariantId(v.getProductVariantId())
                        .color(v.getColor())
                        .size(v.getSize())
                        .price(v.getPrice())
                        .stockQuantity(v.getStockQuantity())
                        .build())
                .toList() ;
        return ApiResponse.ok(response,"success with productId: " + productId);
    }


    public ApiResponse<ProductVariantResponseDTO> updateProductVariant(Integer productId,
                                                                       Integer id,
                                                                       ProductVariantUpdateRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(id, product)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductVariant not found for product " + productId
                ));
        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }
        if (request.getPrice() != null) {
            variant.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }
        productVariantRepository.save(variant);

        return ApiResponse.ok(ProductVariantResponseDTO.builder()
                .productId(product.getProductId())
                .productVariantId(variant.getProductVariantId())
                .color(variant.getColor())
                .size(variant.getSize())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .build());
    }

    public ApiResponse<Void> deleteProductVariant(Integer productId, Integer id) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(id, product)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductVariant not found for product " + productId
                ));
        productVariantRepository.delete(variant);
        return ApiResponse.ok(null, "Product variant successfully deleted");
    }

    public ApiResponse<ProductVariantResponseDTO> addProductVariant(Integer productId, ProductVariantCreationRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Product not found with id: " + productId
                        )
                );

        ProductVariant variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .color(request.getColor())
                .size(request.getSize())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build());

        return ApiResponse.ok(ProductVariantResponseDTO.builder()
                .productVariantId(variant.getProductVariantId())
                .productId(variant.getProduct().getProductId())
                .color(variant.getColor())
                .size(variant.getSize())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .build());
    }

}
