package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.dto.request.ITCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.InventoryTransactionResponse;
import vn.edu.ptit.shoe_shop.entity.InventoryTransaction;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.repository.InventoryTransactionRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryTransactionService {
    InventoryTransactionRepository inventoryTransactionRepository;
    ProductVariantRepository productVariantRepository;

    public List<InventoryTransactionResponse> search(UUID variantId,
                                                     ITEnum type,
                                                     Instant fromDate,
                                                     Instant toDate) {

        if (variantId != null && !productVariantRepository.existsById(variantId)) {
            throw new BadRequestException("Don't have product variant with id " + variantId);
        }
        List<InventoryTransaction> list = inventoryTransactionRepository.search(
                variantId,
                type,
                fromDate,
                toDate
        );

        return list.stream()
                .map(this::toResponse)
                .toList();
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

            ProductVariant variant = transaction.getVariant();

            int newQuantity = variant.getQuantity()
                    + transaction.getQuantityChange() ;

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


}
