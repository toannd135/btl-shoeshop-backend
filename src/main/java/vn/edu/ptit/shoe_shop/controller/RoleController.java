package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.RoleSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.RolePageResponseDTO;
import vn.edu.ptit.shoe_shop.service.RoleService;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Role created successfully")
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody @Valid RoleCreateRequestDTO roleCreateRequestDTO) {
        log.info("START createRole with name: {}", roleCreateRequestDTO.getName());

        RoleResponseDTO res = this.roleService.createRole(roleCreateRequestDTO);

        log.info("END createRole successfully. Role ID: {}, Name: {}", res.getRoleId(), res.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/roles/{id}")
    @ApiMessage("Role updated successfully")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable UUID id,
                                                      @Valid @RequestBody RoleUpdateRequestDTO roleRequestDTO) {
        log.info("START updateRole with ID: {}, data: {}", id, roleRequestDTO);

        RoleResponseDTO roleResponseDTO = this.roleService.updateRole(roleRequestDTO, id);

        log.info("END updateRole successfully for ID: {}", id);
        return ResponseEntity.ok().body(roleResponseDTO);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Role deleted successfully")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        log.info("START deleteRole with ID: {}", id);

        this.roleService.deleteRole(id);

        log.info("END deleteRole successfully for ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Role retrieved successfully")
    public ResponseEntity<RoleResponseDTO> fetchRole(@PathVariable UUID id) {
        log.info("START fetchRole with ID: {}", id);

        RoleResponseDTO roleResponseDTO = this.roleService.fetchRole(id);

        log.info("END fetchRole successfully for ID: {}", id);
        return ResponseEntity.ok().body(roleResponseDTO);
    }

    @GetMapping("/roles")
    @ApiMessage("get all roles successfully")
    public ResponseEntity<RolePageResponseDTO> fetchAllRoles(@ModelAttribute @Valid RoleSearchRequestDTO roleSearchRequestDTO,
                                                             Pageable pageable) {
        log.info("START fetchAllRoles with search criteria: {}, page: {}, size: {}, sort: {}", roleSearchRequestDTO,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        RolePageResponseDTO response = this.roleService.searchRoles(roleSearchRequestDTO, pageable);

        log.info("END fetchAllRoles successfully. Total elements: {}, total pages: {}", response.getTotal(), response.getPages());
        return ResponseEntity.ok().body(response);
    }
}