package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptit.shoe_shop.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    boolean existsByCategoryName(String categoryName);
}
