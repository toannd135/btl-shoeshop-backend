package vn.edu.ptit.shoe_shop.service;
import vn.edu.ptit.shoe_shop.dto.request.CategoryCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.CategoryUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDTO create(CategoryCreateRequestDTO request);
    CategoryResponseDTO update(UUID id, CategoryUpdateRequestDTO request);
    void delete(UUID id);
    CategoryResponseDTO getById(UUID id);
    List<CategoryResponseDTO> getAll();

}
