package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.ChangePurchaseOrderItemRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.POSummaryResponse;
import vn.edu.ptit.shoe_shop.dto.response.PurchaseOrderResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.POPageResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderService {
    PurchaseOrderResponse createPO(UUID supplierId, PurchaseOrderCreateRequestDTO request);
    PurchaseOrderResponse updatePO(UUID poId, PurchaseOrderUpdateRequestDTO request);
    PurchaseOrderResponse changeItemsToPO(UUID poId, ChangePurchaseOrderItemRequestDTO request);
    PurchaseOrderResponse deleteItem(UUID poId, UUID itemId);
    POPageResponseDTO getPage(Pageable pageable);
    PurchaseOrderResponse getById(UUID poId);
}
