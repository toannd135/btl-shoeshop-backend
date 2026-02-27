package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ProductCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.ProductPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.ProductService;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @GetMapping("/{id}")
    @ApiMessage("Product retrieved successfully")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
        ProductResponseDTO res = productService.getById(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/all")
    @ApiMessage("Product list retrieved successfully")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAll());
    }

    @PostMapping
    @ApiMessage("Product created successfully")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @ModelAttribute @Valid ProductCreateRequestDTO requestDTO) throws IOException {

        ProductResponseDTO res = productService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/{id}")
    @ApiMessage("Product updated successfully")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id,
            @ModelAttribute @Valid ProductUpdateRequestDTO requestDTO) throws IOException {

        ProductResponseDTO res = productService.update(id, requestDTO);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Product deleted successfully")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    @ApiMessage("Product page retrieved successfully")
    public ResponseEntity<ProductPageResponseDTO> getProductPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort
    )
    {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort));
        return ResponseEntity.ok().body(productService.getPage(pageable));
    }
    @GetMapping("/search")
    public ResponseEntity<ProductPageResponseDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<BigDecimal> sizes,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort).descending());
        return ResponseEntity.ok().body(
                productService.search(keyword, minPrice, maxPrice, sizes, colors, pageable)
        );
    }
}
