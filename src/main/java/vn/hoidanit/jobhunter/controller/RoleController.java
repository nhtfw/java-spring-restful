package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("create role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {

        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("Tên đã tồn tại");
        }

        role = this.roleService.handleCreateRole(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/roles")
    @ApiMessage("update role")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {

        if (!this.roleService.existById(role.getId())) {
            throw new IdInvalidException("ID không tồn tại");
        }

        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("Tên đã tồn tại");
        }

        return ResponseEntity.ok().body(this.roleService.handleUpdateRole(role));
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoles(@Filter Specification<Role> spec, Pageable pageable) {

        ResultPaginationDTO res = this.roleService.fetchAllRoles(spec, pageable);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete role")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) throws IdInvalidException {

        if (!this.roleService.existById(id)) {
            throw new IdInvalidException("ID không tồn tại");
        }

        this.roleService.deleteById(id);

        return ResponseEntity.ok().body(null);
    }

}
