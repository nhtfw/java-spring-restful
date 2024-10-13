package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private FilterBuilder filterBuilder;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    @PostMapping("/resumes")
    @ApiMessage("Create Resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);

        if (!isIdExist) {
            throw new IdInvalidException("User/job không tồn tại");
        }

        ResCreateResumeDTO res = this.resumeService.handleCreateResume(resume);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/resume")
    @ApiMessage("Update Resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume)
            throws IdInvalidException {

        if (this.resumeService.fetchResumeByID(resume.getId()) == null) {
            throw new IdInvalidException("Resume không tồn tại");
        }

        ResUpdateResumeDTO res = this.resumeService.handleUpdateResume(resume);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/resume/{id}")
    @ApiMessage("Delete Resume")
    public ResponseEntity<Void> deleteResume(@PathVariable long id) throws IdInvalidException {
        if (this.resumeService.fetchResumeByID(id) == null) {
            throw new IdInvalidException("Resume không tồn tại");
        }

        this.resumeService.handleDeleteResume(id);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch Resume")
    public ResponseEntity<ResFetchResumeDTO> fetchResume(@PathVariable long id) throws IdInvalidException {

        if (this.resumeService.fetchResumeByID(id) == null) {
            throw new IdInvalidException("Resume không tồn tại");
        }

        ResFetchResumeDTO res = this.resumeService.getResume(this.resumeService.fetchResumeByID(id));

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all Resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {

        // arr chứa id
        List<Long> arrJobIds = null;
        // lấy email của ng đăng nhập
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        // tìm tới ng dùng
        User currentUser = this.userService.handleGetUserByUsername(email);

        if (currentUser != null) {
            // tìm cty của ng dùng
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                // tìm tất cả các job của cty
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    // nạp tất cả id của các job vào array
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        /*
         * filterSpecificationConverter: Đây là một đối tượng có nhiệm vụ chuyển đổi từ
         * một cấu hình bộ lọc (filterBuilder) thành một đối tượng Specification<Resume>
         */
        /*
         * Đối tượng filterBuilder được sử dụng để bắt đầu xây dựng bộ lọc. Ở đây, nó
         * chọn trường job trong đối tượng Resume.
         */
        /*
         * in(): Đây là phương thức được sử dụng để kiểm tra xem giá trị của trường job
         * có nằm trong danh sách giá trị được cung cấp (arrJobIds) hay không.
         * 
         * ngoài ra có thể thay in bằng nhiều điều kiện khác
         */
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        // kết hợp spec
        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Fetch Resume by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        ResultPaginationDTO res = this.resumeService.fetchResumeByUser(pageable);
        return ResponseEntity.ok().body(res);
    }

}
