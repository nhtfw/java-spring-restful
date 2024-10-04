package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobRepository jobRepository;

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill newSkill) {

        Skill skill = new Skill();
        skill.setId(newSkill.getId());
        skill.setName(newSkill.getName());

        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {

        Page<Skill> page = this.skillRepository.findAll(spec, pageable);

        Meta meta = new Meta();
        ResultPaginationDTO res = new ResultPaginationDTO();

        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        res.setMeta(meta);
        res.setResult(page.getContent());

        return res;
    }

    public Optional<Skill> fetchSkillById(long id) {
        return this.skillRepository.findById(id);
    }

    public Job handleCreateJob(ReqCreateJobDTO req) {
        Job job = new Job();

        for (ReqCreateJobDTO.Skill skill : req.getSkills()) {
            if (this.skillRepository.existsById(skill.getId())) {
                job.getSkills().add(this.skillRepository.findById(skill.getId()).get());
            }
        }

        job.setName(req.getName());
        job.setLocation(req.getLocation());
        job.setSalary(req.getSalary());
        job.setQuantity(req.getQuantity());
        job.setLevel(req.getLevel());
        job.setDescription(req.getDescription());
        job.setStartDate(req.getStartDate());
        job.setEndDate(req.getEndDate());
        job.setActive(req.isActive());

        return this.jobRepository.save(job);
    }

}
