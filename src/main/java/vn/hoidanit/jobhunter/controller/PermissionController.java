package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Create Permissions")
    public ResponseEntity<Permission> createPermissions(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission p = this.permissionService.handleCreatePermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update Permissions")
    public ResponseEntity<Permission> updatePermissions(@RequestBody Permission permission) throws IdInvalidException {

        if (!this.permissionService.existById(permission.getId())) {
            throw new IdInvalidException("Id không tồn tại");
        }

        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission p = this.permissionService.handleUpdatePermission(permission);

        return ResponseEntity.ok().body(p);
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all Permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {

        ResultPaginationDTO res = this.permissionService.fetchAllPermissions(spec, pageable);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete Permissions")
    public ResponseEntity<Void> deletePermissions(@PathVariable long id) throws IdInvalidException {

        if (!this.permissionService.existById(id)) {
            throw new IdInvalidException("Id không tồn tại");
        }

        this.permissionService.deleteById(id);

        return ResponseEntity.ok().body(null);
    }

}
