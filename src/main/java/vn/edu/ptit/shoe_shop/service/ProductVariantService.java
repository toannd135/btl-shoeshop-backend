package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
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

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;
    RedisTemplate<String, Object> redisTemplate;

    public ProductVariantResponseDTO getProductVariant(UUID productId, UUID variantId) {

        String key = variantKey(productId, variantId);

        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            if (NULL_MARKER.equals(cached)) {
                throw new ResourceNotFoundException("ProductVariant not found");
            }
            return (ProductVariantResponseDTO) cached;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant variant = productVariantRepository
                .findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> {
                    redisTemplate.opsForValue().set(key, NULL_MARKER, NULL_TTL);
                    return new ResourceNotFoundException("ProductVariant not found");
                });

        ProductVariantResponseDTO response = toResponse(variant);

        redisTemplate.opsForValue().set(key, response, randomTtl(VARIANT_TTL));

        return response;
    }

    public List<ProductVariantResponseDTO> getAllProductVariant(UUID productId) {

        String listKey = variantListKey(productId);

        Object cached = redisTemplate.opsForValue().get(listKey);

        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<ProductVariantResponseDTO> result =
                    (List<ProductVariantResponseDTO>) cached;
            return result;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<ProductVariantResponseDTO> result =
                productVariantRepository.findByProduct(product)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        redisTemplate.opsForValue().set(
                listKey,
                result,
                randomTtl(VARIANT_TTL)
        );

        return result;
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

        ProductVariantResponseDTO response = toResponse(variant);

        redisTemplate.opsForValue().set(
                variantKey(productId, variantId),
                response,
                randomTtl(VARIANT_TTL)
        );
        redisTemplate.delete(variantListKey(productId));
        return response;
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

        redisTemplate.delete("productVariants::" + productId + "_" + variantId);
        redisTemplate.delete(variantListKey(productId));
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
        // xóa list cache
        redisTemplate.delete(variantListKey(productId));

        // xóa null cache
        redisTemplate.delete(variantKey(productId, variant.getProductVariantId()));
        return toResponse(variant);
    }

    public List<ProductVariantResponseDTO> alertLowStock(Integer quantity) {
        return productVariantRepository.findByQuantityLessThan(quantity)
                .stream().map(this::toResponse).toList();
    }

    private static final Duration VARIANT_TTL = Duration.ofMinutes(2);
    private static final String NULL_MARKER = "NULL";
    private static final Duration NULL_TTL = Duration.ofSeconds(30);
    private Duration randomTtl(Duration base) {
        long jitter = ThreadLocalRandom.current().nextLong(30, 120);
        return base.plusSeconds(jitter);
    }
    private String variantKey(UUID productId, UUID variantId) {
        return "productVariants::" + productId + "_" + variantId;
    }

    private String variantListKey(UUID productId) {
        return "productVariants::product_" + productId;
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
