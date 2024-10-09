package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionsRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionsRepository permissionsRepository;

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role role) {

        if (role.getPermissions() != null) {
            // lấy ra danh sách id
            List<Long> reqPermissions = role.getPermissions().stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionsRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(role);
    }

    public Role handleUpdateRole(Role role) {
        Optional<Role> rOptional = this.roleRepository.findById(role.getId());
        if (rOptional.isPresent()) {
            Role currentRole = rOptional.get();

            if (role.getPermissions() != null) {
                List<Long> reqPermissions = role.getPermissions().stream().map(item -> item.getId())
                        .collect(Collectors.toList());

                List<Permission> dbPermissions = this.permissionsRepository.findByIdIn(reqPermissions);
                role.setPermissions(dbPermissions);
            }

            currentRole.setName(role.getName());
            currentRole.setDescription(role.getDescription());
            currentRole.setActive(role.isActive());
            currentRole.setPermissions(role.getPermissions());

            return this.roleRepository.save(currentRole);
        }

        return null;
    }

    public boolean existById(long id) {
        return this.roleRepository.existsById(id);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        Page<Role> page = this.roleRepository.findAll(spec, pageable);

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        res.setMeta(meta);
        res.setResult(page.get());

        return res;
    }

    public void deleteById(long id) {
        this.roleRepository.deleteById(id);
    }

}
