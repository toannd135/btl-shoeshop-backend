package vn.edu.ptit.shoe_shop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.mapper.ProductMapper;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.service.RecommendService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceImpl implements RecommendService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ProductResponseDTO> getRecommendProduct() {
        UUID userId = SecurityUtils.getCurrentUserId();
        try {
            Object recObject = stringRedisTemplate.opsForValue().get("rec:" + userId);
            if (recObject == null) {
                log.warn("No recommendation found in Redis for user {}", userId);
                return new ArrayList<>();
            }

            String rec = recObject.toString();
            if (rec.isEmpty()) return new ArrayList<>();

            List<String> productIds = Arrays.asList(rec.split(","));
            List<Product> products = productRepository.findByProductIdIn(productIds);
            return products.stream()
                    .map(productMapper::toResponse)
                    .toList();

        } catch (Exception e) {
            log.error("Error while fetching recommendations for user {} : {}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ProductResponseDTO> getTopProducts() {
        try {
            Object recObject = stringRedisTemplate.opsForValue().get("rec:default");
            if (recObject == null) {
                log.warn("No recommendation found in Redis");
                return new ArrayList<>();
            }

            String rec = recObject.toString();
            if (rec.isEmpty()) return new ArrayList<>();

            List<String> productIds = Arrays.asList(rec.split(","));
            List<Product> products = productRepository.findByProductIdIn(productIds);
            return products.stream()
                    .map(productMapper::toResponse)
                    .toList();

        } catch (Exception e) {
            log.error("Error while fetching recommendations {}", e.getMessage());
            return new ArrayList<>();
        }
    }

}
