package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.service.PermissionService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Permission created successfully")
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody @Valid PermissionCreateRequestDTO permissionCreateRequestDTO) {
        PermissionResponseDTO res = this.permissionService.createPermission(permissionCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }


}
