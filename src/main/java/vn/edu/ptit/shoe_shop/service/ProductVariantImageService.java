package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantImageResponseDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ProductVariantImageService {
    ProductVariantImageResponseDTO create(UUID productId,
                                                 UUID variantId,
                                                 ProductVariantImageCreateRequestDTO request) throws IOException;
    ProductVariantImageResponseDTO update(UUID productId,
                                                 UUID variantId,
                                                 UUID imageId,
                                                 ProductVariantImageUpdateRequestDTO request) throws IOException;
    List<ProductVariantImageResponseDTO> getAllImage(UUID productId,
                                                            UUID variantId);
    ProductVariantImageResponseDTO getImageById(UUID productId,
                                                       UUID variantId,
                                                       UUID imageId);
    void deleteImage(UUID productId, UUID variantId, UUID imageId);

}
