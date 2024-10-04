package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping("/skill")
    @ApiMessage("create skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill newSkill) throws IdInvalidException {

        if (jobService.isNameExist(newSkill.getName())) {
            throw new IdInvalidException(newSkill.getName() + " đã được sử dụng");
        }

        Skill skill = jobService.handleCreateSkill(newSkill);
        return ResponseEntity.ok().body(skill);
    }

    @PutMapping("/skill")
    @ApiMessage("update skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill newSkill) throws IdInvalidException {

        if (!jobService.fetchSkillById(newSkill.getId()).isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }

        if (jobService.isNameExist(newSkill.getName())) {
            throw new IdInvalidException(newSkill.getName() + " đã được sử dụng");
        }

        Skill skill = jobService.handleUpdateSkill(newSkill);

        return ResponseEntity.ok().body(skill);
    }

    @DeleteMapping("/skill/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable long id) throws IdInvalidException {

        if (!this.jobService.fetchSkillById(id).isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }

        this.jobService.handleDeleteSkill(id);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/skill")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkills(Pageable pageable,
            @Filter Specification<Skill> spec) {

        ResultPaginationDTO res = this.jobService.fetchAllSkills(spec, pageable);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/skill/{id}")
    @ApiMessage("fetch skill")
    public ResponseEntity<Skill> fetchSkill(@PathVariable long id) {

        Skill skill = this.jobService.fetchSkillById(id).isPresent() ? this.jobService.fetchSkillById(id).get() : null;

        return ResponseEntity.ok().body(skill);
    }

    @PostMapping("/job")
    @ApiMessage("create job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job newJob) {

        ResCreateJobDTO job = this.jobService.handleCreateJob(newJob);

        return ResponseEntity.ok().body(job);
    }

    @PutMapping("/job")
    @ApiMessage("update job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job newJob) throws IdInvalidException {
        if (!this.jobService.fetchJobById(newJob.getId()).isPresent()) {
            throw new IdInvalidException("Job không tồn tại");
        }

        ResUpdateJobDTO res = this.jobService.handleUpdateJob(newJob);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/job/{id}")
    @ApiMessage("delete job")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        if (!this.jobService.fetchJobById(id).isPresent()) {
            throw new IdInvalidException("Job không tồn tại");
        }

        this.jobService.handleDeleteJob(id);

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> fetchJob(@PathVariable long id) throws IdInvalidException {
        if (!this.jobService.fetchJobById(id).isPresent()) {
            throw new IdInvalidException("Job không tồn tại");
        }

        Job job = this.jobService.fetchJobById(id).get();
        return ResponseEntity.ok().body(job);
    }

    @GetMapping("/job")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobs(@Filter Specification<Job> spec, Pageable pageable) {

        ResultPaginationDTO res = this.jobService.fetchAllJobs(spec, pageable);

        return ResponseEntity.ok().body(res);
    }

}
