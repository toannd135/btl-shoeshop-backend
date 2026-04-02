package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.AddSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.SupplierUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateSupplierVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierResponse addSupplier(SupplierCreateRequestDTO request);
    SupplierResponse updateSupplier(UUID supplierId, SupplierUpdateRequestDTO request);
    void deleteSupplier(UUID supplierId);
    SupplierResponse getSupplier(UUID supplierId);
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse addVariant(UUID supplierId, AddSupplierVariantRequestDTO request);
    SupplierResponse updateVariant(UUID supplierId,UUID variantId,
                                          UpdateSupplierVariantRequestDTO request);
    void deleteVariant(UUID supplierId, UUID variantId);
}
