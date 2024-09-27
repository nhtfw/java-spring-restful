package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public List<Company> fetchAllCompanies() {
        return this.companyRepository.findAll();
    }

    public ResultPaginationDTO fetchAllCompanies(Pageable pageable) {

        Page<Company> page = this.companyRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();

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
        Meta meta = new Meta();

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
        this.companyRepository.deleteById(id);
    }

}
