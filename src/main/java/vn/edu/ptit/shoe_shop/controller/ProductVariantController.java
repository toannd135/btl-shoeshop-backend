package vn.edu.ptit.shoe_shop.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantCreationRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantResponseDTO;
import vn.edu.ptit.shoe_shop.service.ProductVariantService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/products/{productId}/variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantController {
    ProductVariantService productVariantService;

    @PostMapping
    public ApiResponse<ProductVariantResponseDTO> create(@PathVariable Integer productId,
                                                      @RequestBody ProductVariantCreationRequestDTO request) {
        return productVariantService.addProductVariant(productId, request);

    }

    @GetMapping
    public ApiResponse<List<ProductVariantResponseDTO>> getAll(@PathVariable Integer productId) {
        return productVariantService.getAllProductVariant(productId);
    }

    @GetMapping("/{variantId}")
    public ApiResponse<ProductVariantResponseDTO> getDetail(@PathVariable Integer productId,
                                                         @PathVariable Integer variantId) {
        return productVariantService.getProductVariant(productId, variantId);
    }

    @PutMapping("/{variantId}")
    public ApiResponse<ProductVariantResponseDTO> update(@PathVariable Integer productId,
                                                         @PathVariable Integer variantId,
                                                         @RequestBody ProductVariantUpdateRequestDTO request) {
        return productVariantService.updateProductVariant(productId,variantId,request);

    }

    @DeleteMapping("/{variantId}")
    public ApiResponse<Void> delete(@PathVariable Integer productId,@PathVariable Integer variantId) {
        return productVariantService.deleteProductVariant(productId,variantId);

    }

}
