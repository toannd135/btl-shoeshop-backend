package vn.edu.ptit.shoe_shop.common.init;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
            permissions.add(new Permission("Create a product", "/api/v1/products", "POST", "PRODUCTS"));
            permissions.add(new Permission("Update a product", "/api/v1/products/{id}", "PUT", "PRODUCTS"));
            permissions.add(new Permission("Delete a product", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
            permissions.add(new Permission("Get a products", "/api/v1/products/{id}", "GET", "PRODUCTS"));
            permissions.add(new Permission("Get all products", "/api/v1/products", "GET", "PRODUCTS"));
            permissions.add(new Permission("Get a products by admin", "/api/v1/admin/products/{id}", "GET", "PRODUCTS"));
            permissions.add(new Permission("Get all products by admin", "/api/v1/admin/products", "GET", "PRODUCTS"));
            //category
            permissions.add(new Permission("Create a category", "/api/v1/categories", "POST", "CATEGORIES"));
            permissions.add(new Permission("Update a category", "/api/v1/categories/{id}", "PUT", "CATEGORIES"));
            permissions.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            permissions.add(new Permission("Get a category", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            permissions.add(new Permission("Get all categories", "/api/v1/categories", "GET", "CATEGORIES"));
            permissions.add(new Permission("Get a category by admin", "/api/v1/admin/categories/{id}", "GET", "CATEGORIES"));
            permissions.add(new Permission("Get all categories by admin", "/api/v1/admin/categories", "GET", "CATEGORIES"));

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
            permissions.add(new Permission("Login", "/api/v1/auth/login", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("Get refresh token", "/api/v1/auth/refresh", "GET", "AUTHENTICATIONS"));
            permissions.add(new Permission("Get account", "/api/v1/auth/account", "GET", "AUTHENTICATIONS"));
            permissions.add(new Permission("Logout",  "/api/v1/auth/logout", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("Register", "/api/v1/auth/register", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("Forgot password", "/api/v1/auth/forgot-password", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("OTP verify", "/api/v1/auth/verify-otp", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("Reset password", "/api/v1/auth/reset-password", "POST", "AUTHENTICATIONS"));
            permissions.add(new Permission("Change password", "/api/v1/auth/password-change",  "POST", "AUTHENTICATIONS"));
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

            this.permissionRepository.saveAll(permissions);
        }
        if(countRole == 0){
            List<Permission> allPermissions = new ArrayList<>();
            Role role = new Role();
            role.setName("ADMIN");
            role.setDescription("Admin full permission");
            role.setStatus(StatusEnum.ACTIVE);
            role.setPermissions(allPermissions);
            this.roleRepository.save(role);
        }
        if(countUser == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setFirstName("admin");
            adminUser.setLastName("admin");
            adminUser.setUsername("admin123");
            adminUser.setPhone("0987654321");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            Role adminRole = this.roleRepository.findByName(RoleEnum.ADMIN.name())
                    .orElseThrow(() -> new IllegalStateException("role not found"));
            if(adminUser != null){
                adminUser.setRole(adminRole);
            }
            this.userRepository.save(adminUser);
        }
    }
}
