package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantImageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.ProductVariantImage;
import vn.edu.ptit.shoe_shop.common.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantImageRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.service.Cloudinary.UploadImageFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductVariantImageService {
    ProductVariantImageRepository productVariantImageRepository;
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;
    UploadImageFile uploadImageFile;

    @Caching(
            put = {
                    @CachePut(
                            value = "variantImages",
                            key = "#productId + '_' + #variantId + '_' + #result.imageId"
                    )
            },
            evict = {
                    @CacheEvict(
                            value = "variantImages",
                            key = "'variant_' + #productId + '_' + #variantId"
                    )
            }
    )
    public ProductVariantImageResponseDTO create(UUID productId,
                                                 UUID variantId,
                                                 ProductVariantImageCreateRequestDTO request) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductVariant variant = productVariantRepository.findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        ProductVariantImage image = ProductVariantImage.builder()
                .productVariant(variant)
                .build();
        String imageURL = uploadImageFile.uploadImage(request.getImage(),"variant_image",variantId);
        image.setImageUrl(imageURL);
        ProductVariantImage primaryImage =
                productVariantImageRepository.findByProductVariantAndIsPrimaryTrue(variant).orElse(null);
        if (primaryImage == null) {
            image.setIsPrimary(true);
        } else {
            if (Boolean.TRUE.equals(request.getIsPrimary())) {

                primaryImage.setIsPrimary(false);
                productVariantImageRepository.save(primaryImage);
                image.setIsPrimary(true);

            } else {
                image.setIsPrimary(false);
            }
        }
        if (request.getStatus() != null) {
            image.setStatus(request.getStatus());
        }
        productVariantImageRepository.save(image);
        return toResponse(image);
    }
    @Caching(
            put = {
                    @CachePut(
                            value = "variantImages",
                            key = "#productId + '_' + #variantId + '_' + #imageId"
                    )
            },
            evict = {
                    @CacheEvict(
                            value = "variantImages",
                            key = "'variant_' + #productId + '_' + #variantId"
                    )
            }
    )
    public ProductVariantImageResponseDTO update(UUID productId,
                                                 UUID variantId,
                                                 UUID imageId,
                                                 ProductVariantImageUpdateRequestDTO request) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductVariant variant = productVariantRepository.findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        ProductVariantImage image = productVariantImageRepository.findByImageIdAndProductVariant(imageId, variant)
                .orElseThrow(() -> new ResourceNotFoundException("image not found"));

        if (request.getImage() != null) {
            String imageURL = uploadImageFile.uploadImage(request.getImage(),"variant_image",variantId);
            image.setImageUrl(imageURL);
        }

        if (Boolean.TRUE.equals(request.getIsPrimary())) {

            ProductVariantImage primaryImage =
                    productVariantImageRepository
                            .findByProductVariantAndIsPrimaryTrue(variant)
                            .orElse(null);

            if (primaryImage != null &&
                    !primaryImage.getImageId().equals(image.getImageId())) {

                primaryImage.setIsPrimary(false);
            }

            image.setIsPrimary(true);
        }
        if (request.getStatus() != null) {
            image.setStatus(request.getStatus());
        }
        productVariantImageRepository.save(image);
        return toResponse(image);
    }
    @Cacheable(
            value = "variantImages",
            key = "'variant_' + #productId + '_' + #variantId"
    )
    public List<ProductVariantImageResponseDTO> getAllImage(UUID productId,
                                                            UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductVariant variant = productVariantRepository.findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        List<ProductVariantImage> images = productVariantImageRepository.findByProductVariant(variant);

        return images.stream().map(this::toResponse).toList();
    }
    @Caching(evict = {
            @CacheEvict(
                    value = "variantImages",
                    key = "#productId + '_' + #variantId + '_' + #imageId"
            ),
            @CacheEvict(
                    value = "variantImages",
                    key = "'variant_' + #productId + '_' + #variantId"
            )
    })
    public void deleteImage(UUID productId, UUID variantId, UUID imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductVariant variant = productVariantRepository.findByProductVariantIdAndProduct(variantId, product)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
        ProductVariantImage image = productVariantImageRepository.findByImageIdAndProductVariant(imageId, variant)
                .orElseThrow(() -> new ResourceNotFoundException("image not found"));
        productVariantImageRepository.delete(image);
    }

    private ProductVariantImageResponseDTO toResponse(ProductVariantImage image) {
        return ProductVariantImageResponseDTO.builder()
                .productVariantId(image.getProductVariant().getProductVariantId())
                .imageURL(image.getImageUrl())
                .isPrimary(image.getIsPrimary())

                .status(image.getStatus())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }


}
