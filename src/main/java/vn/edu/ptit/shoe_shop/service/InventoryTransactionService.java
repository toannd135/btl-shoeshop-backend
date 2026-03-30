package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.ITCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.InventoryTransactionResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.InventoryTransactionPageResponseDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface InventoryTransactionService {
    InventoryTransactionPageResponseDTO search(
            UUID variantId,
            ITEnum type,
            Instant fromDate,
            Instant toDate,
            Pageable pageable);
    InventoryTransactionResponse create(ITCreateRequestDTO request);
    InventoryTransactionResponse updateStatus(UUID itId, ITStatusEnum status);
}
