package vn.edu.ptit.shoe_shop.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ProductCreationRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        return productService.getProduct(id);
    }
    @GetMapping("/all")
    public ApiResponse<List<ProductResponseDTO>> getAllProducts() {
        return productService.getAllProducts();
    }
    @PostMapping
    public ApiResponse<ProductResponseDTO> createProduct(@RequestBody ProductCreationRequestDTO RequestDTO) {
        return productService.createProduct(RequestDTO);
    }
    @PutMapping("/{id}")
    public ApiResponse<ProductResponseDTO> updateProduct(@PathVariable Integer id,@RequestBody ProductUpdateRequestDTO productUpdateRequestDTO) {
        return productService.updateProduct(id, productUpdateRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }
}
