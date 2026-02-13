package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantResponseDTO;
import vn.edu.ptit.shoe_shop.service.ProductVariantService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/products/{productId}/variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantController {

    ProductVariantService productVariantService;

    @PostMapping
    @ApiMessage("Product variant created successfully")
    public ResponseEntity<ProductVariantResponseDTO> createProductVariant(
            @PathVariable UUID productId,
            @RequestBody @Valid ProductVariantCreateRequestDTO request
    ) {
        ProductVariantResponseDTO res =
                productVariantService.addProductVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    @ApiMessage("Product variants retrieved successfully")
    public ResponseEntity<List<ProductVariantResponseDTO>> getAllProductVariants(
            @PathVariable UUID productId
    ) {
        List<ProductVariantResponseDTO> res =
                productVariantService.getAllProductVariant(productId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/{variantId}")
    @ApiMessage("Product variant retrieved successfully")
    public ResponseEntity<ProductVariantResponseDTO> getProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {
        ProductVariantResponseDTO res =
                productVariantService.getProductVariant(productId, variantId);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/{variantId}")
    @ApiMessage("Product variant updated successfully")
    public ResponseEntity<ProductVariantResponseDTO> updateProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @RequestBody @Valid ProductVariantUpdateRequestDTO request
    ) {
        ProductVariantResponseDTO res =
                productVariantService.updateProductVariant(productId, variantId, request);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/{variantId}")
    @ApiMessage("Product variant deleted successfully")
    public ResponseEntity<Void> deleteProductVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {
        productVariantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}
