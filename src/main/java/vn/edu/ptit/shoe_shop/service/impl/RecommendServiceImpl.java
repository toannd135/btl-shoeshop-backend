package vn.edu.ptit.shoe_shop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceImpl implements RecommendService {
    private final RedisTemplate redisTemplate;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDTO> getRecommendProduct() {
        UUID userId = SecurityUtils.getCurrentUserId();
        try {
            String rec = redisTemplate.opsForValue().get("rec:" + userId).toString();
            if (rec == null || rec.isEmpty()) {
                log.warn("No recommendation found in Redis for user {}", userId);
                return new ArrayList<>();
            }
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

}
