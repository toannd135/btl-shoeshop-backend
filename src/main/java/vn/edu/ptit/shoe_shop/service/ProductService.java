package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    UploadImageFile uploadImageFile;

    @Caching(evict = {
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "products_page", allEntries = true),
            @CacheEvict(value = "product_search", allEntries = true)
    })
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

        return toResponse(savedProduct);
    }

    @Caching(
            put = @CachePut(value = "products", key = "#id"),
            evict = {
                    @CacheEvict(value = "products_page", allEntries = true),
                    @CacheEvict(value = "product_search", allEntries = true)
            }
    )
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
        return toResponse(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products_page", allEntries = true),
            @CacheEvict(value = "product_search", allEntries = true)
    })
    public void delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    public List<ProductResponseDTO> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::toResponse)
                .toList();

    }

    @Cacheable(
            value = "product_search",
            key = "T(String).format('%s-%s-%s-%s-%s-%s-%s-%s',"
                    + "#keyword ?: '',"
                    + "#minPrice ?: 0,"
                    + "#maxPrice ?: 0,"
                    + "#sizes ?: '[]',"
                    + "#colors ?: '[]',"
                    + "#pageable.pageNumber,"
                    + "#pageable.pageSize,"
                    + "(#pageable.sort.isUnsorted() ? 'default' : #pageable.sort.toString()))",
            unless = "#result.items.isEmpty()"
    )
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

        return response;
    }

    @Cacheable(
            value = "products_page",
            key = "#pageable != null ? " +
                    "#pageable.pageNumber + '-' + " +
                    "#pageable.pageSize + '-' + " +
                    "(#pageable.sort.isUnsorted() ? 'default' : #pageable.sort) : " +
                    "'0-10-default'",
            unless = "#result.items.isEmpty()"
    )
    public ProductPageResponseDTO getPage(Pageable pageable) {

        Pageable pageableToUse = normalizePageable(pageable);

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
