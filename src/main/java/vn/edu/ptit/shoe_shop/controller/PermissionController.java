package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.service.PermissionService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

import java.util.UUID;

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


    @PutMapping("/permissions/{id}")
    @ApiMessage("Permission updated successfully")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@Valid @RequestBody PermissionUpdateRequestDTO permissionRequestDTO,
                                                                  @PathVariable UUID id){
        PermissionResponseDTO permissionResponseDTO = this.permissionService.updatePermission(permissionRequestDTO, id);
        return ResponseEntity.ok().body(permissionResponseDTO);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Permission deleted successfully")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id){
        this.permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Permission retrieved successfully")
    public ResponseEntity<PermissionResponseDTO> getPermission(@PathVariable UUID id){
        PermissionResponseDTO permissionResponseDTO = this.permissionService.fetchPermission(id);
        return ResponseEntity.ok().body(permissionResponseDTO);
    }

}
