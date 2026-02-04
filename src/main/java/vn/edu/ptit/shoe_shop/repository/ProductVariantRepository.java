package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Integer> {

    List<ProductVariant> findByProduct(Product product);

    Optional<ProductVariant> findByProductVariantIdAndProduct(Integer id, Product product);
}
