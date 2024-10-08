package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/resume")
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

    @GetMapping("/resume/{id}")
    @ApiMessage("Fetch Resume")
    public ResponseEntity<ResFetchResumeDTO> fetchResume(@PathVariable long id) throws IdInvalidException {

        if (this.resumeService.fetchResumeByID(id) == null) {
            throw new IdInvalidException("Resume không tồn tại");
        }

        ResFetchResumeDTO res = this.resumeService.getResume(this.resumeService.fetchResumeByID(id));

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {

        ResultPaginationDTO res = this.resumeService.fetchAllResume(spec, pageable);

        return ResponseEntity.ok().body(res);
    }

}
