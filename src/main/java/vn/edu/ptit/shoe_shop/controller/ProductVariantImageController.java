package vn.edu.ptit.shoe_shop.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ProductVariantImageResponseDTO> create(@PathVariable UUID productId,
                                                                 @PathVariable UUID variantId,
                                                                 @ModelAttribute ProductVariantImageCreateRequestDTO request) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productVariantImageService.create(productId,variantId,request));
    }

    @PutMapping(value = "/{imageId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductVariantImageResponseDTO> update(@PathVariable UUID productId,
                                                                 @PathVariable UUID variantId,
                                                                 @PathVariable UUID imageId,
                                                                 @ModelAttribute ProductVariantImageUpdateRequestDTO request) throws IOException {
        return ResponseEntity.ok()
                .body(productVariantImageService.update(productId,variantId,imageId,request));
    }

    @GetMapping
    public  ResponseEntity<List<ProductVariantImageResponseDTO>> getAllImage(@PathVariable UUID productId,
                                                                             @PathVariable UUID variantId){
        return ResponseEntity.ok().body(productVariantImageService.getAllImage(productId,variantId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID productId,@PathVariable UUID variantId,@PathVariable UUID imageId){
        productVariantImageService.deleteImage(productId,variantId,imageId);
        return ResponseEntity.noContent().build();
    }
}
