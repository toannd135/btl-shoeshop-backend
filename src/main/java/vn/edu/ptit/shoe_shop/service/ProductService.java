package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.dto.request.ProductCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.ProductPageResponseDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO create(ProductCreateRequestDTO request) throws IOException;
    ProductResponseDTO update(UUID id, ProductUpdateRequestDTO request) throws IOException;
    List<ProductResponseDTO> getAll();
    ProductResponseDTO getById(UUID id);
    void delete(UUID id);
    public ProductPageResponseDTO search(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<BigDecimal> sizes,
            List<String> colors,
            UUID categoryId,
            GenderEnum gender,
            String brand,
            Pageable pageable
    );
    ProductPageResponseDTO getPage(Pageable pageable);
}
