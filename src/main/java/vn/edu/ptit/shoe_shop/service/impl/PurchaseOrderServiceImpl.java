package vn.edu.ptit.shoe_shop.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.dto.request.ChangePurchaseOrderItemRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.POItemResponse;
import vn.edu.ptit.shoe_shop.dto.response.POSummaryResponse;
import vn.edu.ptit.shoe_shop.dto.response.PurchaseOrderResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.POPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.*;
import vn.edu.ptit.shoe_shop.repository.*;
import vn.edu.ptit.shoe_shop.service.PurchaseOrderService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    PurchaseOrderRepository purchaseOrderRepository;
    SupplierRepository supplierRepository;
    SupplierVariantRepository supplierVariantRepository;
    POItemRepository poItemRepository;
    InventoryTransactionRepository inventoryTransactionRepository;
    ProductVariantRepository productVariantRepository;

    public PurchaseOrderResponse createPO(UUID supplierId,PurchaseOrderCreateRequestDTO request) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(()->new BadRequestException("Supplier Not Found"));
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .build();
        if (request.getNote() != null) {
            purchaseOrder.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }
        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        return toResponse(saved);
    }

    public PurchaseOrderResponse updatePO(UUID poId, PurchaseOrderUpdateRequestDTO request) {
        // 1. Lấy PO với lock
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByIdWithLock(poId)
                .orElseThrow(() -> new BadRequestException("PO not found"));

        // 2. Nếu đã DELIVERED thì không cho sửa
        if (purchaseOrder.getStatus().equals(OrderStatusEnum.DELIVERED)) {
            throw new BadRequestException("Cannot update order because it has already been delivered");
        }

        if (request.getNote() != null) {
            purchaseOrder.setNote(request.getNote());
        }
        if (request.getExpectedDeliveryDate() != null) {
            purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        }

        boolean willBeDelivered = request.getStatus() != null && request.getStatus().equals(OrderStatusEnum.DELIVERED);
        if (willBeDelivered) {
            // Nhập kho
            applyStockImport(purchaseOrder);
            purchaseOrder.setStatus(OrderStatusEnum.DELIVERED);
        } else if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }

        return toResponse(purchaseOrder);
    }

    public PurchaseOrderResponse changeItemsToPO(UUID poId, ChangePurchaseOrderItemRequestDTO request) {

        PurchaseOrder po = purchaseOrderRepository.findByIdWithLockAndItems(poId)
                .orElseThrow(() -> new BadRequestException("Purchase Order not found"));

        // 2. Kiểm tra trạng thái PO có cho phép sửa không
        if (po.getStatus().equals(OrderStatusEnum.DELIVERED) || po.getStatus().equals(OrderStatusEnum.CANCELLED)) {
            throw new BadRequestException("Cannot modify items of a purchase order that is already " + po.getStatus());
        }

        // 3. Tìm SupplierVariant
        SupplierVariant supplierVariant = supplierVariantRepository
                .findBySupplier_SupplierIdAndVariant_ProductVariantId(
                        po.getSupplier().getSupplierId(),
                        request.getVariantId())
                .orElseThrow(() -> new BadRequestException("Supplier doesn't supply this variant"));

        // 4. Xử lý item
        POItem poItem = poItemRepository.findByPurchaseOrderAndVariant(po, supplierVariant).orElse(null);

        if (poItem == null) {
            // Thêm mới
            if (request.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be positive");
            }
            poItem = POItem.builder()
                    .purchaseOrder(po)
                    .variant(supplierVariant)
                    .quantity(request.getQuantity())
                    .build();
            po.getListPOItems().add(poItem);
            // Lưu item
            poItemRepository.save(poItem);
        } else {
            // Cập nhật
            int newQuantity = poItem.getQuantity() + request.getQuantity();
            if (newQuantity <= 0) {
                po.getListPOItems().remove(poItem);
                poItemRepository.delete(poItem);
            } else {
                poItem.setQuantity(newQuantity);
                poItemRepository.save(poItem);
            }
        }

        return toResponse(po);
    }
    public PurchaseOrderResponse deleteItem(UUID poId, UUID itemId) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BadRequestException("PO not found"));

        if (purchaseOrder.getStatus() == OrderStatusEnum.DELIVERED) {
            throw new BadRequestException("Cannot modify delivered PO");
        }
        POItem item = purchaseOrder.getListPOItems()
                .stream()
                .filter(i -> i.getVariant()
                        .getVariant()
                        .getProductVariantId()
                        .equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item doesn't exist in purchase order"));

        purchaseOrder.getListPOItems().remove(item);
        poItemRepository.delete(item);
        return toResponse(purchaseOrder);
    }

    public POPageResponseDTO getPage(Pageable pageable) {
        // Chuẩn hóa pageable (nếu cần)
        Pageable pageableToUse = normalizePageable(pageable);

        Page<PurchaseOrder> page = purchaseOrderRepository.findAllWithSupplier(pageableToUse);

        List<POSummaryResponse> items = page.getContent().stream()
                .map(po -> POSummaryResponse.builder()
                        .poId(po.getPoId())
                        .supplierId(po.getSupplier().getSupplierId())
                        .status(po.getStatus())
                        .createdAt(po.getCreatedAt())
                        .updatedAt(po.getUpdatedAt())
                        .build())
                .toList();

        POPageResponseDTO response = new POPageResponseDTO();
        response.setItems(items);
        response.setPage(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());
        return response;
    }

    public PurchaseOrderResponse getById(UUID poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByIdWithDetails(poId)
                .orElseThrow(() -> new BadRequestException("PO not found"));
        return toResponse(purchaseOrder);
    }

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

    private void applyStockImport(PurchaseOrder purchaseOrder) {
        for (POItem item : purchaseOrder.getListPOItems()) {
            ProductVariant variant = productVariantRepository.findByIdWithLock(
                            item.getVariant().getVariant().getProductVariantId())
                    .orElseThrow(() -> new BadRequestException("Variant not found for item in PO"));

            variant.setQuantity(variant.getQuantity() + item.getQuantity());

            createInventoryTransaction(variant, item.getQuantity(), purchaseOrder.getPoId());
        }
    }
    private void createInventoryTransaction(ProductVariant variant, int quantity, UUID poId) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .type(ITEnum.PURCHASE)
                .quantityChange(quantity)
                .variant(variant)
                .reason("Import from supplier: PO " + poId)
                .status(ITStatusEnum.COMPLETED)
                .build();
        inventoryTransactionRepository.save(transaction);
    }
    private PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {

        List<POItem> items = purchaseOrder.getListPOItems();
        List<POItemResponse> res = items.stream()
                .map(i -> POItemResponse.builder()
                        .variantId(i.getVariant().getVariant().getProductVariantId())
                        .cost(i.getVariant().getCost())
                        .quantity(i.getQuantity())
                        .build())
                .toList();
        return PurchaseOrderResponse.builder()
                .supplierId(purchaseOrder.getSupplier().getSupplierId())
                .poId(purchaseOrder.getPoId())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .note(purchaseOrder.getNote())
                .status(purchaseOrder.getStatus())
                .items(res)

                .createdAt(purchaseOrder.getCreatedAt())
                .updatedAt(purchaseOrder.getUpdatedAt())
                .createdBy(purchaseOrder.getCreatedBy())
                .updatedBy(purchaseOrder.getUpdatedBy())
                .build();
    }


}
