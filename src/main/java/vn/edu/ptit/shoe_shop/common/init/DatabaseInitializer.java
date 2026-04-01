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



                this.permissionRepository.saveAll(permissions);
            }
            if(countRole == 0){
                List<Permission> allPermissions = new ArrayList<>();
                Role role = new Role();
                role.setName("ADMIN");
                role.setCode("ROLE_ADMIN");
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
                adminUser.setProvider(ProviderEnum.SERVER);
                adminUser.setStatus(StatusEnum.ACTIVE);
                adminUser.setPassword(this.passwordEncoder.encode("Admin123@"));
                Role adminRole = this.roleRepository.findByName(RoleEnum.ADMIN.name())
                        .orElseThrow(() -> new IllegalStateException("role not found"));
                if(adminUser != null){
                    adminUser.setRole(adminRole);
                }
                this.userRepository.save(adminUser);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } 
    }
}
