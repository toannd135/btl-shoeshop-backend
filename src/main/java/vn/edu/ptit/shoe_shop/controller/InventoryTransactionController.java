package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.ITCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.InventoryTransactionResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.InventoryTransactionPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.InventoryTransactionService;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory-transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class InventoryTransactionController {

    InventoryTransactionService inventoryTransactionService;

    @GetMapping("/search")
    public ResponseEntity<InventoryTransactionPageResponseDTO> search(
            @RequestParam(required = false) UUID variantId,
            @RequestParam(required = false) ITEnum type,
            @RequestParam(required = false) Instant fromDate,
            @RequestParam(required = false) Instant toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return ResponseEntity.ok(inventoryTransactionService.search(
                variantId, type, fromDate, toDate, pageable));
    }

    @PostMapping
    public ResponseEntity<InventoryTransactionResponse> create(@RequestBody @Valid ITCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryTransactionService.create(request));
    }

    @PutMapping("/{itId}/status")
    public ResponseEntity<InventoryTransactionResponse> updateStatus(
            @PathVariable UUID itId,
            @RequestParam ITStatusEnum status) {

        return ResponseEntity.ok(
                inventoryTransactionService.updateStatus(itId, status)
        );
    }
}