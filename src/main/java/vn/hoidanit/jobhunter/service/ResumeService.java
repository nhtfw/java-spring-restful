package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.ResumeRepository;

import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public ResCreateResumeDTO handleCreateResume(Resume resume) {

        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();

        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume) {
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        Optional<Resume> resumeOp = this.resumeRepository.findById(resume.getId());

        if (resumeOp.isPresent()) {
            Resume newResume = resumeOp.get();

            newResume.setStatus(resume.getStatus());

            resume = this.resumeRepository.save(newResume);

            res.setUpdatedAt(resume.getUpdatedAt());
            res.setUpdatedBy(resume.getUpdatedBy());
        }

        return res;
    }

    public Resume fetchResumeByID(long id) {
        return this.resumeRepository.findById(id).isPresent() ? this.resumeRepository.findById(id).get() : null;
    }

    public void handleDeleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }
}
