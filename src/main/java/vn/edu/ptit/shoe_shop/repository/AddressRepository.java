package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.entity.Address;
import vn.edu.ptit.shoe_shop.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user")
    void updateIsDefaultFalseByUser(@Param("user") User user);

    boolean existsByUser(User user);

    Optional<Address> findByAddressId(UUID id);
    
    List<Address> findByUser(User user);
}
