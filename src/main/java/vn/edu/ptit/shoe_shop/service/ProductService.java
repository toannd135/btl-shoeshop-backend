package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import vn.edu.ptit.shoe_shop.dto.request.ProductCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.ProductUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.ProductPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.common.exception.ResourceNotFoundException;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.repository.impl.ProductSpecs;
import vn.edu.ptit.shoe_shop.service.Cloudinary.UploadImageFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    UploadImageFile uploadImageFile;
    RedisTemplate<String, Object> redisTemplate;

    public ProductResponseDTO create(ProductCreateRequestDTO request) throws IOException {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException("Category not found"));
        }
        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .description(request.getDescription())
                .category(category)
                .gender(request.getGender())
                .build();
        if (request.getImage() != null) {
            String imageUrl = uploadImageFile.uploadImage(request.getImage(), "products", product.getProductId());
            product.setImageUrl(imageUrl);
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        Product savedProduct = productRepository.save(product);

        invalidateProductCache(savedProduct.getProductId());

        return toResponse(savedProduct);
    }

    public ProductResponseDTO update(UUID id, ProductUpdateRequestDTO request) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (request.getName() != null)
            product.setName(request.getName());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getGender() != null) {
            product.setGender(request.getGender());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getImage() != null) {
            String imageUrl = uploadImageFile.uploadImage(request.getImage(), "products", product.getProductId());
            product.setImageUrl(imageUrl);
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        Product saved = productRepository.save(product);
        invalidateProductCache(saved.getProductId());
        return toResponse(saved);
    }


    public void delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
        invalidateProductCache(id);
    }

    public ProductResponseDTO getById(UUID id) {

        String cacheKey = "products::" + id;

        // 1. Check cache
        ProductResponseDTO cached =
                (ProductResponseDTO) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            return cached;
        }

        // 2. Query DB
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductResponseDTO response = toResponse(product);

        // 3. Set cache
        redisTemplate.opsForValue().set(
                cacheKey,
                response,
                randomTtl(Duration.ofMinutes(10))
        );

        return response;
    }

    public List<ProductResponseDTO> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::toResponse)
                .toList();

    }

    public ProductPageResponseDTO search(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<BigDecimal> sizes,
            List<String> colors,
            Pageable pageable
    ) {

        if (pageable == null || pageable.getPageSize() <= 0) {
            int pageNum = (pageable == null) ? 0 : pageable.getPageNumber();
            pageable = PageRequest.of(pageNum, 5);
        }
        List<BigDecimal> sortedSizes = sizes == null ? List.of() :
                sizes.stream().sorted().toList();

        List<String> sortedColors = colors == null ? List.of() :
                colors.stream().sorted().toList();

        String cacheKey = String.format("%s-%s-%s-%s-%s-%s-%s-%s",
                keyword != null ? keyword : "",
                minPrice != null ? minPrice : 0,
                maxPrice != null ? maxPrice : 0,
                sortedSizes,
                sortedColors,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isUnsorted()
                        ? "default"
                        : pageable.getSort().toString()
        );

        String redisKey = "product_search::" + cacheKey;

        // 1. TRY CACHE
        ProductPageResponseDTO cached =
                (ProductPageResponseDTO) redisTemplate.opsForValue().get(redisKey);

        if (cached != null) {
            return cached;
        }
        Specification<Product> spec = Specification.allOf(
                ProductSpecs.keyword(keyword),
                ProductSpecs.priceBetween(minPrice, maxPrice),
                ProductSpecs.variationSizeIn(sizes),
                ProductSpecs.variationColorIn(colors)
        );

        Page<Product> page = productRepository.findAll(spec, pageable);

        List<ProductResponseDTO> items = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        ProductPageResponseDTO response = new ProductPageResponseDTO();
        response.setItems(items);
        response.setPage(page.getNumber()+1);
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());

        // 3. SET CACHE
        if (!response.getItems().isEmpty()) {

            redisTemplate.opsForValue().set(
                    redisKey,
                    response,
                    randomTtl(Duration.ofMinutes(5))
            );

            redisTemplate.opsForSet()
                    .add("product_search:index", redisKey);
        }

        return response;
    }

    public ProductPageResponseDTO getPage(Pageable pageable) {

        Pageable pageableToUse = normalizePageable(pageable);

        String cacheKey =
                pageableToUse.getPageNumber() + "-" +
                        pageableToUse.getPageSize() + "-" +
                        (pageableToUse.getSort().isUnsorted()
                                ? "default"
                                : pageableToUse.getSort().toString());

        String redisKey = "products_page::" + cacheKey;

        // 1. TRY CACHE
        ProductPageResponseDTO cached =
                (ProductPageResponseDTO) redisTemplate.opsForValue().get(redisKey);

        if (cached != null) {
            return cached;
        }
        Page<Product> page = productRepository.findAll(pageableToUse);

        List<ProductResponseDTO> items = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        ProductPageResponseDTO response = new ProductPageResponseDTO();
        response.setItems(items);
        response.setPage(page.getNumber()+1);
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());

        // 3. SET CACHE TTL SO LE
        if (!response.getItems().isEmpty()) {

            redisTemplate.opsForValue().set(
                    redisKey,
                    response,
                    randomTtl(Duration.ofMinutes(10))
            );

            redisTemplate.opsForSet()
                    .add("products_page:index", redisKey);
        }
        return response;
    }

    private Pageable normalizePageable(Pageable pageable) {

        int defaultPage = 0;
        int defaultSize = 5;
        Sort defaultSort = Sort.by("createdAt").descending();

        if (pageable == null) {
            return PageRequest.of(defaultPage, defaultSize, defaultSort);
        }

        int page = pageable.getPageNumber() < 0 ? defaultPage : pageable.getPageNumber();
        int size = pageable.getPageSize() <= 0 ? defaultSize : pageable.getPageSize();

        Sort sort = pageable.getSort().isUnsorted() ? defaultSort : pageable.getSort();

        return PageRequest.of(page, size, sort);
    }

    private void invalidateProductCache(UUID productId) {

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

                        // 1. xóa cache chi tiết
                        redisTemplate.delete("products::" + productId);
                        
                        redisTemplate.delete("productVariants::product_" + productId);

                        // 2. xóa page cache
                        Set<Object> pageKeys =
                                redisTemplate.opsForSet().members("products_page:index");

                        if (pageKeys != null && !pageKeys.isEmpty()) {
                            redisTemplate.delete(
                                    pageKeys.stream()
                                            .map(Object::toString)
                                            .collect(Collectors.toSet())
                            );
                        }

                        redisTemplate.delete("products_page:index");

                        // 3. xóa search cache
                        Set<Object> searchKeys =
                                redisTemplate.opsForSet().members("product_search:index");

                        if (searchKeys != null && !searchKeys.isEmpty()) {
                            redisTemplate.delete(
                                    searchKeys.stream()
                                            .map(Object::toString)
                                            .collect(Collectors.toSet())
                            );
                        }

                        redisTemplate.delete("product_search:index");
                    }
                }
        );
    }

    private Duration randomTtl(Duration time) {
        long jitter = ThreadLocalRandom.current().nextLong(30, 180);
        return time.plusSeconds(jitter);
    }

    private ProductResponseDTO toResponse(Product product) {
        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .brand(product.getBrand())
                .gender(product.getGender())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
