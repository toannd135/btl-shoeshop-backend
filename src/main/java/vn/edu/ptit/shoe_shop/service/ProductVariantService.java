package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.ProductVariantCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProductVariantService {
    ProductVariantResponseDTO getProductVariant(UUID productId, UUID variantId);
    List<ProductVariantResponseDTO> getAllProductVariant(UUID productId);
    ProductVariantResponseDTO updateProductVariant(UUID productId,
                                                          UUID variantId,
                                                          ProductVariantUpdateRequestDTO request);
    void deleteProductVariant(UUID productId, UUID variantId);
    ProductVariantResponseDTO addProductVariant(UUID productId,
                                                       ProductVariantCreateRequestDTO request);
    List<ProductVariantResponseDTO> alertLowStock(Integer quantity);
}
