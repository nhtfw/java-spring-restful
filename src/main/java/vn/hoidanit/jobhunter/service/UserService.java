package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> comOptional = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }
        return this.userRepository.save(user);
    };

    public User handleUpdateUser(User newUser) {
        User user = this.userRepository.findTop1ById(newUser.getId());
        // vì đã có id nên thay vì create, chương trình sẽ update
        if (user != null) {

            user.setName(newUser.getName());
            user.setGender(newUser.getGender());
            user.setAddress(newUser.getAddress());
            user.setAge(newUser.getAge());

            // check company
            if (user.getCompany() != null) {
                Optional<Company> comOptional = this.companyService.fetchCompanyById(user.getCompany().getId());
                user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
            }

            return this.userRepository.save(user);
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public ResultPaginationDTO fetchAllUser(Pageable pageable) {
        Page<User> page = this.userRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // số trang hiện tại
        meta.setPage(page.getNumber() + 1);
        // số phần tử mỗi trang
        meta.setPageSize(page.getSize());
        // tổng số trang
        meta.setPages(page.getTotalPages());
        // tổng số phần tử
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(page.getContent());

        return rs;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec) {
        List<User> users = this.userRepository.findAll(spec);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        rs.setMeta(meta);
        rs.setResult(users);

        return rs;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // số trang hiện tại
        meta.setPage(pageable.getPageNumber() + 1);
        // số phần tử mỗi trang
        meta.setPageSize(pageable.getPageSize());
        // tổng số trang
        meta.setPages(page.getTotalPages());
        // tổng số phần tử
        meta.setTotal(page.getTotalElements());

        rs.setMeta(meta);

        List<ResUserDTO> users = new ArrayList<ResUserDTO>();
        for (User user : page.getContent()) {
            ResUserDTO ru = new ResUserDTO();

            ru.setId(user.getId());
            ru.setName(user.getName());
            ru.setEmail(user.getEmail());
            ru.setGender(user.getGender());
            ru.setAddress(user.getAddress());
            ru.setAge(user.getAge());
            ru.setCreateAt(user.getCreatedAt());
            ru.setUpdatedAt(user.getUpdatedAt());

            if (user.getCompany() != null) {
                ResUserDTO.Company company = new ResUserDTO.Company();
                company.setId(user.getCompany().getId());
                company.setName(user.getCompany().getName());

                ru.setCompany(company);
            } else {
                ru.setCompany(null);
            }

            users.add(ru);
        }
        rs.setResult(users);

        return rs;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO ru = new ResCreateUserDTO();

        ru.setId(user.getId());
        ru.setName(user.getName());
        ru.setEmail(user.getEmail());
        ru.setGender(user.getGender());
        ru.setAddress(user.getAddress());
        ru.setAge(user.getAge());
        ru.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            ResCreateUserDTO.Company company = new ResCreateUserDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            ru.setCompany(company);
        }

        return ru;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO ru = new ResUserDTO();
        ResUserDTO.Company company = new ResUserDTO.Company();

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            ru.setCompany(company);
        }

        ru.setId(user.getId());
        ru.setName(user.getName());
        ru.setEmail(user.getEmail());
        ru.setGender(user.getGender());
        ru.setAddress(user.getAddress());
        ru.setAge(user.getAge());
        ru.setCreateAt(user.getCreatedAt());
        ru.setUpdatedAt(user.getUpdatedAt());

        return ru;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO ru = new ResUpdateUserDTO();

        if (user.getCompany() != null) {
            ResUpdateUserDTO.Company company = new ResUpdateUserDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            ru.setCompany(company);
        }

        ru.setId(user.getId());
        ru.setName(user.getName());
        ru.setGender(user.getGender());
        ru.setAddress(user.getAddress());
        ru.setAge(user.getAge());
        ru.setUpdatedAt(user.getUpdatedAt());

        return ru;
    }

    public void updateUserToken(String token, String email) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
