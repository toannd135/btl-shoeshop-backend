package vn.edu.ptit.shoe_shop.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductVariantImageUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductVariantImageResponseDTO;
import vn.edu.ptit.shoe_shop.service.ProductVariantImageService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/variants/{variantId}/images")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductVariantImageController {
    ProductVariantImageService productVariantImageService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Create product variant image successfully")
    public ResponseEntity<ProductVariantImageResponseDTO> create(@PathVariable UUID productId,
                                                                 @PathVariable UUID variantId,
                                                                 @ModelAttribute ProductVariantImageCreateRequestDTO request) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productVariantImageService.create(productId,variantId,request));
    }

    @PutMapping(value = "/{imageId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Update product variant image successfully")
    public ResponseEntity<ProductVariantImageResponseDTO> update(@PathVariable UUID productId,
                                                                 @PathVariable UUID variantId,
                                                                 @PathVariable UUID imageId,
                                                                 @ModelAttribute ProductVariantImageUpdateRequestDTO request) throws IOException {
        return ResponseEntity.ok()
                .body(productVariantImageService.update(productId,variantId,imageId,request));
    }

    @GetMapping
    @ApiMessage("Get all product variant images successfully")
    public  ResponseEntity<List<ProductVariantImageResponseDTO>> getAllImage(@PathVariable UUID productId,
                                                                             @PathVariable UUID variantId){
        return ResponseEntity.ok().body(productVariantImageService.getAllImage(productId,variantId));
    }

    @GetMapping("/{imageId}")
    @ApiMessage("Get product variant image successfully")
    public ResponseEntity<ProductVariantImageResponseDTO> getVariantImage(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @PathVariable UUID imageId
    ) {
        ProductVariantImageResponseDTO res =
                productVariantImageService.getImageById(productId, variantId,imageId);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/{imageId}")
    @ApiMessage("Delete product variant image successfully")
    public ResponseEntity<Void> delete(@PathVariable UUID productId,@PathVariable UUID variantId,@PathVariable UUID imageId){
        productVariantImageService.deleteImage(productId,variantId,imageId);
        return ResponseEntity.noContent().build();
    }
}
