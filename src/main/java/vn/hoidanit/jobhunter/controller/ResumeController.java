package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        if (this.userService.fetchUserById(resume.getUser().getId()) == null) {
            throw new IdInvalidException("User/job không tồn tại");
        }

        if (!this.jobService.fetchJobById(resume.getJob().getId()).isPresent()) {
            throw new IdInvalidException("User/job không tồn tại");
        }

        ResCreateResumeDTO res = this.resumeService.handleCreateResume(resume);

        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/resume")
    @ApiMessage("Update Resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@Valid @RequestBody Resume resume)
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

}
