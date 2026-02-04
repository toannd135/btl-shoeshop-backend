package vn.edu.ptit.shoe_shop.controller;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.CategoryRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.CategoryResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.repository.CategoryRepository;
import vn.edu.ptit.shoe_shop.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;
    @GetMapping()
    public ApiResponse<List<CategoryResponseDTO>> getAllCategories(){
        return categoryService.getAll();
    }
    @PostMapping()
    public ApiResponse<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO){
        return categoryService.createCategory(categoryRequestDTO);
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Integer id){
        return categoryService.deleteCategory(id);
    }
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> getCategory(@PathVariable Integer id){
        return categoryService.getCategory(id);
    }
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> updateCategory(@PathVariable Integer id,@RequestBody CategoryRequestDTO categoryRequestDTO){
        return categoryService.updateCategory(id, categoryRequestDTO);
    }
}
