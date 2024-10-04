package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.request.ReqCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

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

        if (jobService.isNameExist(newSkill.getName())) {
            throw new IdInvalidException(newSkill.getName() + " đã được sử dụng");
        }

        Skill skill = jobService.handleUpdateSkill(newSkill);

        return ResponseEntity.ok().body(skill);
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
    public ResponseEntity<Job> createJob(@Valid @RequestBody ReqCreateJobDTO req) {

        Job job = this.jobService.handleCreateJob(req);

        return ResponseEntity.ok().body(job);
    }

}
