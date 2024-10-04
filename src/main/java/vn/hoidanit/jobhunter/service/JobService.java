package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO.Meta;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
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

    public void handleDeleteSkill(long id) {
        // xoas data trong job_skill
        Optional<Skill> skillOp = this.skillRepository.findById(id);
        Skill skill = skillOp.get();
        // skill.getJobs().forEach(job -> job.getSkills().remove(skill));
        // lặp từng job một và xóa đi skill trong job đấy
        for (Job job : skill.getJobs()) {
            job.getSkills().remove(skill);
        }

        // delete skill
        this.skillRepository.delete(skill);
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

    public ResCreateJobDTO handleCreateJob(Job newJob) {
        // check skills
        if (newJob.getSkills() != null) {
            // List<Long> reqSkills = newJob.getSkills().stream().map(x ->
            // x.getId()).collect(Collectors.toList());

            List<Long> skills = new ArrayList<>();
            for (Skill skill : newJob.getSkills()) {
                skills.add(skill.getId());
            }

            // tìm kiếm một tập hợp các thực thể dựa trên một danh sách các giá trị của
            // trường id
            List<Skill> dbSkills = this.skillRepository.findByIdIn(skills);

            newJob.setSkills(dbSkills);
        }

        // save
        Job job = this.jobRepository.save(newJob);

        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLevel(job.getLevel());
        // res.setDescription(job.getDescription());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());

        // chỉ trả ra name của skill
        if (job.getSkills() != null) {
            List<String> skillsName = job.getSkills().stream().map(item -> item.getName()).collect(Collectors.toList());

            res.setSkills(skillsName);
        }

        return res;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO handleUpdateJob(Job newJob) {
        // check skills
        if (newJob.getSkills() != null) {
            List<Long> skills = new ArrayList<>();
            for (Skill skill : newJob.getSkills()) {
                skills.add(skill.getId());
            }

            List<Skill> dbSkills = this.skillRepository.findByIdIn(skills);

            newJob.setSkills(dbSkills);
        }

        // save
        Job job = this.jobRepository.save(newJob);

        ResUpdateJobDTO res = new ResUpdateJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLevel(job.getLevel());
        // res.setDescription(job.getDescription());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());

        if (job.getSkills() != null) {
            List<String> skillsName = job.getSkills().stream()
                    .map(item -> item.getName()).collect(Collectors.toList());

            res.setSkills(skillsName);
        }

        return res;

    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(spec, pageable);

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

}
