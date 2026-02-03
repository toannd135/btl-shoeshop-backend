package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.service.RoleService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Role created suscessfully")
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid RoleCreateRequestDTO roleCreateRequestDTO) {
        RoleResponseDTO res = this.roleService.createRole(roleCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


}
