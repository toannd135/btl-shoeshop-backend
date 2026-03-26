package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ChangePurchaseOrderItemRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PurchaseOrderUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.POSummaryResponse;
import vn.edu.ptit.shoe_shop.dto.response.PurchaseOrderResponse;
import vn.edu.ptit.shoe_shop.service.PurchaseOrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PurchaseOrderController {
    PurchaseOrderService purchaseOrderService;

    @PostMapping("/suppliers/{supplierId}/purchase-orders")
    public ResponseEntity<PurchaseOrderResponse> create(
            @PathVariable UUID supplierId,
            @RequestBody @Valid PurchaseOrderCreateRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseOrderService.createPO(supplierId,request));
    }

    @PutMapping("/purchase-order/{poId}")
    public ResponseEntity<PurchaseOrderResponse> updatePurchaseOrder(
            @PathVariable UUID poId,
            @RequestBody @Valid PurchaseOrderUpdateRequestDTO request){
        return ResponseEntity.ok().body(purchaseOrderService.updatePO(poId,request));
    }

    @GetMapping("/purchase-order/{id}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrder(@PathVariable UUID id){
        return ResponseEntity.ok().body(purchaseOrderService.getById(id));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<POSummaryResponse>> getAll(){
        return ResponseEntity.ok().body(purchaseOrderService.getAll());
    }

    @PostMapping("/purchase-order/{poId}/items")
    public ResponseEntity<PurchaseOrderResponse> changeItems(
            @PathVariable UUID poId,
            @RequestBody ChangePurchaseOrderItemRequestDTO request
    ){
        return ResponseEntity.ok().body(purchaseOrderService.changeItemsToPO(poId,request));
    }

    @DeleteMapping("/purchase-order/{poId}/items/{itemId}")
    public ResponseEntity<PurchaseOrderResponse> deleteItem(
            @PathVariable UUID poId,
            @PathVariable UUID itemId){
        return ResponseEntity.ok().body(purchaseOrderService.deleteItem(poId,itemId));
    }
}
