package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.AddSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.SupplierResponse;
import vn.edu.ptit.shoe_shop.service.SupplierService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/supplier")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SupplierController {
    SupplierService supplierService;

    @GetMapping("/all")
    public ResponseEntity<List<SupplierResponse>> getAll() {
        return ResponseEntity.ok().body(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(supplierService.getSupplier(id));
    }

    @PostMapping()
    public ResponseEntity<SupplierResponse> createSupplier(@RequestBody @Valid
                                                           SupplierCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.addSupplier(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse>  updateSupplierById(@PathVariable UUID id, @RequestBody @Valid
    SupplierUpdateRequestDTO request) {
        return ResponseEntity.ok().body(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SupplierResponse> deleteSupplierById(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<SupplierResponse> AddVariant(@PathVariable UUID id,
                                                       @RequestBody AddSupplierVariantRequestDTO request) {
        return ResponseEntity.ok().body(supplierService.addVariant(id, request));
    }
    @PutMapping("/{id}/add/{variantId}")
    public ResponseEntity<SupplierResponse> UpdateVariant(@PathVariable UUID id,
                                                          @PathVariable UUID variantId,
                                                       @RequestBody UpdateSupplierVariantRequestDTO request) {
        return ResponseEntity.ok().body(supplierService.updateVariant(id,variantId, request));
    }

    @DeleteMapping("/{id}/remove/{variantId}")
    public ResponseEntity<SupplierResponse> RemoveVariant(@PathVariable UUID id,
                                                          @PathVariable UUID variantId) {
        supplierService.deleteVariant(id, variantId);
        return ResponseEntity.noContent().build();
    }
}
