package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.Category;
import vn.edu.ptit.shoe_shop.entity.Product;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Set<Product> findAllByProductIdIn(Set<Integer> productIds);

    List<Product> findAllByCategory(Category category);
}
