package vn.edu.ptit.shoe_shop.common.init;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.ptit.shoe_shop.common.enums.ProviderEnum;
import vn.edu.ptit.shoe_shop.common.enums.RoleEnum;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.PermissionRepository;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {
        private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
                        UserRepository userRepository, PasswordEncoder passwordEncoder) {
                this.permissionRepository = permissionRepository;
                this.roleRepository = roleRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        long countPermission = permissionRepository.count();
        long countRole = roleRepository.count();
        long countUser = userRepository.count();
        try {
            if(countPermission == 0) {
                List<Permission> permissions = permissionRepository.findAll();
                //user
                permissions.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
                permissions.add(new Permission("Update a user", "/api/v1/users/{id}", "PUT", "USERS"));
                permissions.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
                permissions.add(new Permission("Get a user", "/api/v1/users/{id}", "GET", "USERS"));
                permissions.add(new Permission("Get all users", "/api/v1/users", "GET", "USERS"));
                //role
                permissions.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
                permissions.add(new Permission("Update a role", "/api/v1/roles/{id}", "PUT", "ROLES"));
                permissions.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
                permissions.add(new Permission("Get a role", "/api/v1/roles/{id}", "GET", "ROLES"));
                permissions.add(new Permission("Get all roles", "/api/v1/roles", "GET", "ROLES"));
                // permission
                permissions.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
                permissions.add(new Permission("Update a permission", "/api/v1/permissions/{id}", "PUT", "PERMISSIONS"));
                permissions.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
                permissions.add(new Permission("Get a permissions", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
                permissions.add(new Permission("Get all permissions", "/api/v1/permissions", "GET", "PERMISSIONS"));
                // products

                // address
                permissions.add(new Permission("Create a address", "/api/v1/addresses", "POST", "ADDRESSES"));
                permissions.add(new Permission("Update a address", "/api/v1/addresses/{id}", "PUT", "ADDRESSES"));
                permissions.add(new Permission("Delete a address", "/api/v1/addresses/{id}", "DELETE", "ADDRESSES"));
                permissions.add(new Permission("Get a address", "/api/v1/addresses/{id}", "GET", "ADDRESSES"));
                permissions.add(new Permission("Get all addresses", "/api/v1/addresses", "GET", "ADDRESSES"));
                //cart
                permissions.add(new Permission("Create a cart", "/api/v1/items/carts", "POST", "CARTS"));
                permissions.add(new Permission("Update a cart", "/api/v1/carts/items/{id}", "PUT", "CARTS"));
                permissions.add(new Permission("Delete a cart", "/api/v1/carts/items/{id}", "DELETE", "CARTS"));
                permissions.add(new Permission("Get all carts", "/api/v1/carts", "GET", "CARTS"));
                // checkout
                permissions.add(new Permission("check out", "/api/v1/checkout", "GET", "CHECKOUTS"));
                //order
                permissions.add(new Permission("Create a order", "/api/v1/orders", "POST", "ORDERS"));
                permissions.add(new Permission("Update a order", "/api/v1/orders/{id}", "PUT", "ORDERS"));
                permissions.add(new Permission("Delete a order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
                permissions.add(new Permission("Get a order", "/api/v1/orders/{id}", "GET", "ORDERS"));
                permissions.add(new Permission("Get all orders", "/api/v1/orders", "GET", "ORDERS"));
                permissions.add(new Permission("Update status order", "/api/v1/orders/{id}/status", "PATCH", "ORDERS"));
                //coupon
                permissions.add(new Permission("Create a coupon", "/api/v1/coupons", "POST", "COUPONS"));
                permissions.add(new Permission("Update a coupon", "/api/v1/coupons/{id}", "PUT", "COUPONS"));
                permissions.add(new Permission("Delete a coupon", "/api/v1/coupons/{id}", "DELETE", "COUPONS"));
                permissions.add(new Permission("Get a coupon", "/api/v1/coupons/{id}", "GET", "COUPONS"));
                permissions.add(new Permission("Get all coupons", "/api/v1/coupons", "GET", "COUPONS"));
                permissions.add(new Permission("Get a coupon by admin", "/api/v1/admin/coupons/{id}", "GET", "COUPONS"));
                permissions.add(new Permission("Get all coupons by admin", "/api/v1/admin/coupons", "GET", "COUPONS"));
                // login, register, logout, forgot password, reset password, change password
                
                // file
                permissions.add(new Permission("create file",  "/api/v1/files", "POST", "FILES"));
                permissions.add(new Permission("upload single file to cloud", "/api/v1/cloudinary/upload",  "POST", "FILES"));
                permissions.add(new Permission("Upload multi file to cloud", "/api/v1/cloudinary/upload-multiple", " POST", "FILES"));
                // review
                permissions.add(new Permission("Create review", "/api/v1/reviews", "POST", "REVIEWS"));
                permissions.add(new Permission("Get active reviews by products ID", "/api/v1/reviews/products/{productsId}", "GET", "REVIEWS"));
                permissions.add(new Permission("Update review", "/api/v1/reviews/{id}", "PUT", "REVIEWS"));
                permissions.add(new Permission("Soft delete review", "/api/v1/reviews/{id}", "DELETE", "REVIEWS"));
                // review for ADMIN
                permissions.add(new Permission("Get all reviews by products (admin)", "/api/v1/admin/reviews/products/{productsId}", "GET", "REVIEWS"));
                permissions.add(new Permission("Hard delete review (admin)", "/api/v1/admin/reviews/{id}", "DELETE", "REVIEWS"));
                // Module supplier
                permissions.add(new Permission("Get all suppliers", "/api/v1/supplier/all", "GET", "SUPPLIERS"));
                permissions.add(new Permission("Get a supplier by id", "/api/v1/supplier/{id}", "GET", "SUPPLIERS"));
                permissions.add(new Permission("Create a supplier", "/api/v1/supplier", "POST", "SUPPLIERS"));
                permissions.add(new Permission("Update a supplier", "/api/v1/supplier/{id}", "PUT", "SUPPLIERS"));
                permissions.add(new Permission("Delete a supplier", "/api/v1/supplier/{id}", "DELETE", "SUPPLIERS"));
                // Variant of supplier
                permissions.add(new Permission("Add a supplier variant", "/api/v1/supplier/{id}/add", "POST", "SUPPLIERS"));
                permissions.add(new Permission("Update a supplier variant", "/api/v1/supplier/{id}/add/{variantId}", "PUT", "SUPPLIERS"));
                permissions.add(new Permission("Remove a supplier variant", "/api/v1/supplier/{id}/remove/{variantId}", "DELETE", "SUPPLIERS"));
                // Module purchase order
                permissions.add(new Permission("Create a purchase order", "/api/v1/suppliers/{supplierId}/purchase-orders", "POST", "PURCHASE_ORDERS"));
                permissions.add(new Permission("Update a purchase order", "/api/v1/purchase-order/{poId}", "PUT", "PURCHASE_ORDERS"));
                permissions.add(new Permission("Get a purchase order by id", "/api/v1/purchase-order/{id}", "GET", "PURCHASE_ORDERS"));
                permissions.add(new Permission("Get all purchase orders", "/api/v1/purchase-orders", "GET", "PURCHASE_ORDERS"));
                // purchase order Items
                permissions.add(new Permission("Change items in a purchase order", "/api/v1/purchase-order/{poId}/items", "POST", "PURCHASE_ORDERS"));
                permissions.add(new Permission("Delete an item in a purchase order", "/api/v1/purchase-order/{poId}/items/{itemId}", "DELETE", "PURCHASE_ORDERS"));
                //inventory transaction
                permissions.add(new Permission("Search inventory transactions", "/api/v1/inventory-transactions/search", "GET", "INVENTORY_TRANSACTIONS"));
                permissions.add(new Permission("Create an inventory transaction", "/api/v1/inventory-transactions", "POST", "INVENTORY_TRANSACTIONS"));
                permissions.add(new Permission("Update inventory transaction status", "/api/v1/inventory-transactions/{itId}/status", "PUT", "INVENTORY_TRANSACTIONS"));

                                // Chat
                permissions.add(new Permission("Create a default chat when user starts a conversation",
                                "/api/v1/chat/conversations/ensure", "POST", "CHAT"));
                permissions.add(new Permission("Send message to other user", "/api/v1/chat/messages",
                                "POST", "CHAT"));
                permissions.add(new Permission("Get messages in a conversation",
                                "/api/v1/chat/messages?conversationId={conversationId}", "GET",
                                "CHAT"));

                // Chat for admin
                permissions.add(new Permission("Get all conversations for admin",
                                "/api/v1/conversations", "GET", "CHAT"));
                                this.permissionRepository.saveAll(permissions);
                        }
                        if (countRole == 0) {
                                List<Permission> allPermissions = permissionRepository.findAll();
                                Role role = new Role();
                                role.setName("SUPER_ADMIN");
                                role.setCode("ROLE_SUPER_ADMIN");
                                role.setDescription("Super admin full permission");
                                role.setStatus(StatusEnum.ACTIVE);
                                role.setPermissions(allPermissions);
                                this.roleRepository.save(role);
                        }
                        if (countUser == 0) {
                                User adminUser = new User();
                                adminUser.setEmail("superadmin@gmail.com");
                                adminUser.setFirstName("super");
                                adminUser.setLastName("admin");
                                adminUser.setUsername("superadmin123");
                                adminUser.setPhone("0987654321");
                                adminUser.setProvider(ProviderEnum.SERVER);
                                adminUser.setStatus(StatusEnum.ACTIVE);
                                adminUser.setPassword(this.passwordEncoder.encode("SuperAdmin123@"));
                                Role adminRole = this.roleRepository.findByName(RoleEnum.SUPER_ADMIN.name())
                                                .orElseThrow(() -> new IllegalStateException("role not found"));
                                if (adminUser != null) {
                                        adminUser.setRole(adminRole);
                                }
                                this.userRepository.save(adminUser);
                        }
                } catch (Exception e) {
                        log.error(e.getMessage());
                }
        }
}
