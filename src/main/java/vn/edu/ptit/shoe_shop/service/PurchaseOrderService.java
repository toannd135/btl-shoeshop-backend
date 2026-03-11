package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import vn.edu.ptit.shoe_shop.entity.*;
import vn.edu.ptit.shoe_shop.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PurchaseOrderService {
    PurchaseOrderRepository purchaseOrderRepository;
    SupplierRepository supplierRepository;
    SupplierVariantRepository supplierVariantRepository;
    POItemRepository poItemRepository;
    InventoryTransactionRepository inventoryTransactionRepository;

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
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(()-> new BadRequestException("PO not found"));

        if (purchaseOrder.getStatus().equals(OrderStatusEnum.DELIVERED)) {
            throw new BadRequestException("Cannot update order because it has already been delivered");
        }
        if (request.getNote() != null) {
            purchaseOrder.setNote(request.getNote());
        }
        if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }
        if (request.getExpectedDeliveryDate() != null) {
            purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        }

        purchaseOrderRepository.save(purchaseOrder);
        if (request.getStatus() != null && request.getStatus().equals(OrderStatusEnum.DELIVERED)) {
            for (POItem item : purchaseOrder.getListPOItems()) {

                ProductVariant variant = item.getVariant().getVariant();

                variant.setQuantity(variant.getQuantity() + item.getQuantity());

                InventoryTransaction transaction = InventoryTransaction.builder()
                        .type(ITEnum.PURCHASE)
                        .quantityChange(item.getQuantity())
                        .variant(variant)
                        .reason("Import from supplier")
                        .status(ITStatusEnum.COMPLETED)
                        .build();

                inventoryTransactionRepository.save(transaction);
            }
        }
        return toResponse(purchaseOrder);
    }

    @Transactional
    public PurchaseOrderResponse changeItemsToPO(UUID poId, ChangePurchaseOrderItemRequestDTO request) {

        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BadRequestException("Purchase Order not found"));

        SupplierVariant supplierVariant =
                supplierVariantRepository
                        .findBySupplier_SupplierIdAndVariant_ProductVariantId(
                                po.getSupplier().getSupplierId(),
                                request.getVariantId())
                        .orElseThrow(() -> new BadRequestException("Supplier doesn't supply this variant"));

        POItem poItem = poItemRepository
                .findByPurchaseOrderAndVariant(po, supplierVariant)
                .orElse(null);

        if (poItem == null) {

            if (request.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be positive");
            }

            poItem = POItem.builder()
                    .purchaseOrder(po)
                    .variant(supplierVariant)
                    .quantity(request.getQuantity())
                    .build();

            po.getListPOItems().add(poItem);

        } else {

            int newQuantity = poItem.getQuantity() + request.getQuantity();

            if (newQuantity <= 0) {
                po.getListPOItems().remove(poItem);
                poItemRepository.delete(poItem);
            } else {
                poItem.setQuantity(newQuantity);
            }
        }

        return toResponse(po);
    }

    public PurchaseOrderResponse deleteItem(UUID poId, UUID itemId) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BadRequestException("PO not found"));

        POItem item = purchaseOrder.getListPOItems()
                .stream()
                .filter(i -> i.getVariant()
                        .getVariant()
                        .getProductVariantId()
                        .equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Item doesn't exist in purchase order"));

        purchaseOrder.getListPOItems().remove(item);

        return toResponse(purchaseOrder);
    }

    public List<POSummaryResponse> getAll(){
        List<PurchaseOrder> lists = purchaseOrderRepository.findAll();
        return lists.stream()
                .map(po->POSummaryResponse.builder()
                        .createdAt(po.getCreatedAt())
                        .updatedAt(po.getUpdatedAt())
                        .status(po.getStatus())
                        .poId(po.getPoId())
                        .supplierId(po.getSupplier().getSupplierId())
                        .build()).toList();
    }

    public PurchaseOrderResponse getById(UUID poId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BadRequestException("PO not found"));
        return toResponse(purchaseOrder);
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
                .build();
    }


}
