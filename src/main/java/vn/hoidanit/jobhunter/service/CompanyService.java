package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> fetchAllCompanies() {
        return this.companyRepository.findAll();
    }

    public ResultPaginationDTO fetchAllCompanies(Pageable pageable) {

        Page<Company> page = this.companyRepository.findAll(pageable);

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

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {

        Page<Company> page = this.companyRepository.findAll(spec, pageable);

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
        rs.setResult(page.getContent());

        return rs;
    }

    public Optional<Company> fetchCompanyById(long id) {
        return this.companyRepository.findById(id);
    }

    public Company handleUpdateCompany(Company newCompany) {
        Optional<Company> opCompany = this.companyRepository.findById(newCompany.getId());

        if (opCompany.isPresent()) {
            Company currentCompany = opCompany.get();

            currentCompany.setName(newCompany.getName());
            currentCompany.setDescription(newCompany.getDescription());
            currentCompany.setAddress(newCompany.getAddress());
            currentCompany.setLogo(newCompany.getLogo());

            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> cOptional = this.companyRepository.findById(id);
        if (cOptional.isPresent()) {
            Company company = cOptional.get();

            // xoa tat ca nguoi dung roi moi xoa company
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

}
