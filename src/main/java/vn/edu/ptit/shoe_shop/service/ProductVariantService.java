package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.common.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;

    public ProductVariantResponseDTO getProductVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with productId: " + productId));
        ;
        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant not found with variantId: " + variantId));

        return toResponse(variant);
    }

    public List<ProductVariantResponseDTO> getAllProductVariant(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        List<ProductVariant> variants = productVariantRepository.findByProduct(product);
        return variants.stream()
                .map(this::toResponse)
                .toList();
    }


    public ProductVariantResponseDTO updateProductVariant(UUID productId,
                                                          UUID variantId,
                                                          ProductVariantUpdateRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with productId: " + productId));

        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductVariant not found for product " + productId
                ));
        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }
        if (request.getSku() != null) {
            variant.setSku(request.getSku());
        }
        if (request.getQuantity() != null) {
            variant.setQuantity(request.getQuantity());
        }
        if (request.getStatus() != null) {
            variant.setStatus(request.getStatus());
        }
        if (request.getBasePrice() != null) {
            variant.setBasePrice(request.getBasePrice());
        }
        productVariantRepository.save(variant);

        return toResponse(variant);
    }

    public void deleteProductVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with productId: " + productId));
        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductVariant not found for product with variantId: " + variantId
                ));
        productVariantRepository.delete(variant);
    }

    public ProductVariantResponseDTO addProductVariant(UUID productId, ProductVariantCreateRequestDTO request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Product not found with id: " + productId
                        )
                );

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.getSku())
                .color(request.getColor())
                .size(request.getSize())
                .quantity(request.getQuantity())
                .basePrice(request.getBasePrice())
                .build();
        if (request.getStatus() != null) {
            variant.setStatus(request.getStatus());
        }
        productVariantRepository.save(variant);

        return toResponse(variant);
    }

    private ProductVariantResponseDTO toResponse(ProductVariant productVariant) {
        return ProductVariantResponseDTO.builder()
                .productId(productVariant.getProduct().getProductId())
                .productVariantId(productVariant.getProductVariantId())
                .sku(productVariant.getSku())
                .color(productVariant.getColor())
                .size(productVariant.getSize())
                .quantity(productVariant.getQuantity())
                .basePrice(productVariant.getBasePrice())

                .status(productVariant.getStatus())
                .createdAt(productVariant.getCreatedAt())
                .updatedAt(productVariant.getUpdatedAt())
                .build();
    }

}
