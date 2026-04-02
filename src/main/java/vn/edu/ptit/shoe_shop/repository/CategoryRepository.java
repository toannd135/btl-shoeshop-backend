package vn.edu.ptit.shoe_shop.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptit.shoe_shop.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCategoryName(String categoryName);

    boolean existsByParent(Category category);

    boolean existsByCategoryNameAndParent(String categoryName, Category parent);

    boolean existsByCategoryNameAndParentAndCategoryIdNot(String categoryName, Category parent, UUID id);
}
