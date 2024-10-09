package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionsRepository;

@Service
public class PermissionService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    public boolean isPermissionExist(@Valid Permission permission) {
        return this.permissionsRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission handleCreatePermission(@Valid Permission permission) {
        return this.permissionsRepository.save(permission);
    }

    public boolean existById(long id) {
        return this.permissionsRepository.existsById(id);
    }

    public Permission handleUpdatePermission(Permission permission) {
        Optional<Permission> op = this.permissionsRepository.findById(permission.getId());

        if (op.isPresent()) {
            Permission p = op.get();

            p.setId(permission.getId());
            p.setName(permission.getName());
            p.setApiPath(permission.getApiPath());
            p.setMethod(permission.getMethod());
            p.setModule(permission.getModule());

            return this.permissionsRepository.save(p);
        }

        return null;
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionsRepository.findAll(spec, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        res.setMeta(meta);
        res.setResult(page.get());

        return res;
    }

    public void deleteById(long id) {
        // delete permission_role
        Optional<Permission> peOptional = this.permissionsRepository.findById(id);
        if (peOptional.isPresent()) {
            Permission permission = peOptional.get();

            // tìm từng phần tử role trong permission xem có phần tử nào (role) mà danh sách
            // của pt đó có chứa permission hiện tại
            permission.getRoles().forEach(role -> role.getPermissions().remove(permission));

            this.permissionsRepository.delete(permission);
        }
    }
}
