package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.PermissionSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.PermissionPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.PermissionService;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;
    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Permission created successfully")
    public ResponseEntity<PermissionResponseDTO> createPermission(
            @RequestBody @Valid PermissionCreateRequestDTO permissionCreateRequestDTO) {

        log.info("START createPermission with data: {}", permissionCreateRequestDTO);

        PermissionResponseDTO res = this.permissionService.createPermission(permissionCreateRequestDTO);

        log.info("END createPermission successfully with ID: {}", res.getPermissionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/permissions/{id}")
    @ApiMessage("Permission updated successfully")
    public ResponseEntity<PermissionResponseDTO> updatePermission(
            @Valid @RequestBody PermissionUpdateRequestDTO permissionRequestDTO,
            @PathVariable UUID id) {

        log.info("START updatePermission with ID: {} and data: {}", id, permissionRequestDTO);

        PermissionResponseDTO permissionResponseDTO = this.permissionService.updatePermission(permissionRequestDTO, id);

        log.info("END updatePermission successfully for ID: {}", id);

        return ResponseEntity.ok().body(permissionResponseDTO);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Permission deleted successfully")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {

        log.info("START deletePermission with ID: {}", id);

        this.permissionService.deletePermission(id);

        log.info("END deletePermission successfully for ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Permission retrieved successfully")
    public ResponseEntity<PermissionResponseDTO> fetchPermission(@PathVariable UUID id) {

        log.info("START fetchPermission with ID: {}", id);
        PermissionResponseDTO permissionResponseDTO = this.permissionService.fetchPermission(id);
        log.info("END fetchPermission successfully for ID: {}", id);

        return ResponseEntity.ok().body(permissionResponseDTO);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions successfully")
    public ResponseEntity<PermissionPageResponseDTO> fetchAllPermissions(
            @ModelAttribute @Valid PermissionSearchRequestDTO permissionSearchRequestDTO,
            Pageable pageable) {

        log.info("START fetchAllPermissions with search criteria: {}, page: {}, size: {}, sort: {}",
                permissionSearchRequestDTO,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        PermissionPageResponseDTO res = this.permissionService.searchPermissions(permissionSearchRequestDTO, pageable);

        log.info("END fetchAllPermissions successfully. Total elements: {}, total pages: {}", res.getTotal(), res.getPages());
        return ResponseEntity.ok().body(res);
    }
}