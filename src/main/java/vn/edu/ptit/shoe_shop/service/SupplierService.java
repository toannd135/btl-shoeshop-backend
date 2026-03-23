package vn.edu.ptit.shoe_shop.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.dto.request.AddSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.SupplierResponse;
import vn.edu.ptit.shoe_shop.dto.response.SupplierVariantResponse;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.Supplier;
import vn.edu.ptit.shoe_shop.entity.SupplierVariant;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.repository.SupplierRepository;
import vn.edu.ptit.shoe_shop.repository.SupplierVariantRepository;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierService {
    SupplierRepository supplierRepository;
    SupplierVariantRepository supplierVariantRepository;
    ProductVariantRepository productVariantRepository;

    public SupplierResponse addSupplier(SupplierCreateRequestDTO request) {
        Supplier supplier = Supplier.builder()
                .supplierName(request.getSupplierName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }
        Supplier saved = supplierRepository.save(supplier);
        return toResponse(saved);
    }

    public SupplierResponse updateSupplier(UUID supplierId, SupplierUpdateRequestDTO request) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier Not Found"));

        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }
        if (request.getAddress() != null) {
            supplier.setAddress(request.getAddress());
        }
        if (request.getEmail() != null) {
            supplier.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            supplier.setPhone(request.getPhone());
        }
        if (request.getSupplierName() != null) {
            supplier.setSupplierName(request.getSupplierName());
        }
        Supplier updated = supplierRepository.save(supplier);
        return toResponse(updated);

    }

    public void deleteSupplier(UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier Not Found"));
        supplierRepository.delete(supplier);
    }

    public SupplierResponse getSupplier(UUID supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier Not Found"));
        return toResponse(supplier);
    }

    public List<SupplierResponse> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream().map(this::toResponse).toList();
    }

    public SupplierResponse addVariant(UUID supplierId, AddSupplierVariantRequestDTO request) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));

        ProductVariant variant = productVariantRepository.findByProductVariantId(request.getVariantId())
                .orElseThrow(() -> new BadRequestException("Variant not found"));
        SupplierVariant supplierVariants = SupplierVariant.builder()
                .supplier(supplier)
                .variant(variant)
                .cost(request.getCost())
                .build();
        supplierVariantRepository.save(supplierVariants);

        return toResponse(supplier);
    }

    public SupplierResponse updateVariant(UUID supplierId,UUID variantId, UpdateSupplierVariantRequestDTO request) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));

        ProductVariant variant =
                productVariantRepository.findById(variantId)
                        .orElseThrow(() -> new BadRequestException("Variant not found"));

        SupplierVariant supplierVariant = supplierVariantRepository.findBySupplierAndVariant(supplier, variant)
                .orElseThrow(() -> new BadRequestException("Supplier don't supply this variant"));

        if (request.getCost() != null) {
            supplierVariant.setCost(request.getCost());
        }
        if (request.getNote() != null) {
            supplierVariant.setNote(request.getNote());
        }
        supplierVariantRepository.save(supplierVariant);
        return toResponse(supplier);
    }

    public void deleteVariant(UUID supplierId, UUID variantId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));

        ProductVariant variant =
                productVariantRepository.findById(variantId)
                        .orElseThrow(() -> new BadRequestException("Variant not found"));

        SupplierVariant supplierVariant = supplierVariantRepository.findBySupplierAndVariant(supplier, variant)
                .orElseThrow(() -> new BadRequestException("Supplier don't supply this variant"));
        supplierVariantRepository.delete(supplierVariant);
    }

    private SupplierResponse toResponse(Supplier supplier) {

        List<SupplierVariant> variants =
                supplierVariantRepository.findBySupplierFetchVariant(supplier);

        List<SupplierVariantResponse> variantResponses = variants.stream()
                .map(v -> SupplierVariantResponse.builder()
                        .variantId(v.getVariant().getProductVariantId())
                        .color(v.getVariant().getColor())
                        .size(v.getVariant().getSize())
                        .cost(v.getCost())
                        .build())
                .toList();

        return SupplierResponse.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .address(supplier.getAddress())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .status(supplier.getStatus())
                .variants(variantResponses)
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
