package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.ProductPageResponseDTO;

import java.util.List;

public interface RecommendService {
    List<ProductResponseDTO> getRecommendProduct();
    List<ProductResponseDTO> getTopProducts();
}
