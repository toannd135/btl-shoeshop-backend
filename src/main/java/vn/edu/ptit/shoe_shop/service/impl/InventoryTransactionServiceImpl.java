package vn.edu.ptit.shoe_shop.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.dto.request.ITCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.InventoryTransactionResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.InventoryTransactionPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.InventoryTransaction;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.repository.InventoryTransactionRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.repository.impl.InventoryTransactionSpecs;
import vn.edu.ptit.shoe_shop.service.InventoryTransactionService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryTransactionServiceImpl implements InventoryTransactionService {
    InventoryTransactionRepository inventoryTransactionRepository;
    ProductVariantRepository productVariantRepository;

    private Pageable normalizePageable(Pageable pageable) {
        int defaultPage = 0;
        int defaultSize = 10;
        Sort defaultSort = Sort.by("createdAt").descending();

        if (pageable == null) {
            return PageRequest.of(defaultPage, defaultSize, defaultSort);
        }

        int page = pageable.getPageNumber() < 0 ? defaultPage : pageable.getPageNumber();
        int size = pageable.getPageSize() <= 0 ? defaultSize : pageable.getPageSize();
        Sort sort = pageable.getSort().isUnsorted() ? defaultSort : pageable.getSort();

        return PageRequest.of(page, size, sort);
    }

    public InventoryTransactionPageResponseDTO search(
            UUID variantId,
            ITEnum type,
            Instant fromDate,
            Instant toDate,
            Pageable pageable) {

        // Kiểm tra variant nếu có
        if (variantId != null && !productVariantRepository.existsById(variantId)) {
            throw new BadRequestException("Don't have product variant with id " + variantId);
        }

        // Chuẩn hóa pageable
        Pageable pageableToUse = normalizePageable(pageable);

        // Xây dựng Specification
        Specification<InventoryTransaction> spec = Specification.allOf(
                InventoryTransactionSpecs.variantIdEquals(variantId),
                InventoryTransactionSpecs.typeEquals(type),
                InventoryTransactionSpecs.fromDateGreaterThanOrEqual(fromDate),
                InventoryTransactionSpecs.toDateLessThanOrEqual(toDate)
        );

        // Truy vấn
        Page<InventoryTransaction> page = inventoryTransactionRepository.findAll(spec, pageableToUse);

        // Chuyển đổi sang DTO
        List<InventoryTransactionResponse> items = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        InventoryTransactionPageResponseDTO response = new InventoryTransactionPageResponseDTO();
        response.setItems(items);
        response.setPage(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());

        return response;
    }

    public InventoryTransactionResponse create(ITCreateRequestDTO request) {

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new BadRequestException("Variant not found"));

        InventoryTransaction transaction = InventoryTransaction.builder()
                .quantityChange(request.getQuantityChange())
                .type(request.getType())
                .reason(request.getReason())
                .status(ITStatusEnum.PENDING)
                .variant(variant)
                .build();

        inventoryTransactionRepository.save(transaction);

        return toResponse(transaction);
    }

    public InventoryTransactionResponse updateStatus(UUID itId, ITStatusEnum status) {
        InventoryTransaction transaction = inventoryTransactionRepository.findById(itId)
                .orElseThrow(() -> new BadRequestException("Inventory transaction not found"));

        if (transaction.getStatus() != ITStatusEnum.PENDING) {
            throw new BadRequestException("Only PENDING transaction can be updated");
        }

        if (status == ITStatusEnum.COMPLETED) {
            // Load variant với pessimistic lock
            ProductVariant variant = productVariantRepository.findByIdWithLock(
                            transaction.getVariant().getProductVariantId())
                    .orElseThrow(() -> new BadRequestException("Variant not found"));

            int newQuantity = variant.getQuantity() + transaction.getQuantityChange();
            if (newQuantity < 0) {
                throw new BadRequestException("Stock cannot be negative");
            }
            variant.setQuantity(newQuantity);
        }

        transaction.setStatus(status);
        return toResponse(transaction);
    }

    private InventoryTransactionResponse toResponse(InventoryTransaction inT) {
        return InventoryTransactionResponse.builder()
                .itId(inT.getItId())
                .type(inT.getType())
                .reason(inT.getReason())
                .quantityChange(inT.getQuantityChange())
                .variantId(inT.getVariant().getProductVariantId())
                .status(inT.getStatus())
                .build();
    }
        @Transactional
    public void adjustStock(
            UUID variantId,
            int quantityChange,
            ITEnum type,
            UUID referenceId,
            UUID userId,
            String reason
    ) {
        ProductVariant variant = productVariantRepository.findByProductVariantIdForUpdate(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        int newQuantity = variant.getQuantity() + quantityChange;

        if (newQuantity < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        variant.setQuantity(newQuantity);
        this.productVariantRepository.save(variant);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setVariant(variant);
        tx.setType(type);
        tx.setQuantityChange(quantityChange);
        tx.setCreatedBy(userId);
        tx.setReferenceId(referenceId);
        tx.setReason(reason);

        this.inventoryTransactionRepository.save(tx);
    }

  
}
