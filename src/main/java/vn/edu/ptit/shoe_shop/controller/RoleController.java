package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.service.RoleService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Role created successfully")
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid RoleCreateRequestDTO roleCreateRequestDTO) {
        RoleResponseDTO res = this.roleService.createRole(roleCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


    @PutMapping("/roles/{id}")
    @ApiMessage("Role updated successfully")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleUpdateRequestDTO roleRequestDTO) {
        RoleResponseDTO roleResponseDTO = this.roleService.updateRole(roleRequestDTO, id);
        return ResponseEntity.ok().body(roleResponseDTO);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Role deleted successfully")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        this.roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Role retrieved successfully")
    public ResponseEntity<RoleResponseDTO> getRole(@PathVariable UUID id) {
        RoleResponseDTO roleResponseDTO = this.roleService.fetchRole(id);
        return ResponseEntity.ok().body(roleResponseDTO);
    }
}
