package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmailService emailService;

    public Subscriber fetchSubscriberByEmail(String email) {
        if (this.subscriberRepository.findByEmail(email).isPresent()) {
            return this.subscriberRepository.findByEmail(email).get();
        } else {
            return null;
        }
    }

    public Subscriber handleCreateSubscriber(Subscriber subscriber) {
        // check skill
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);

            subscriber.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber subDB, Subscriber subReq) {
        // check skill
        if (subReq.getSkills() != null) {
            List<Long> reqSkills = subReq.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);

            subDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subDB);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();

        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();

                if (listSkills != null && listSkills.size() > 0) {

                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);

                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arrJobs = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        // "job" -> file job.html trong folder resources/templates
                        /*
                         * Vì sao dự án có thể hiểu job là job.html và phải tìm job.html trong folder
                         * templates ? => Vì đó là cấu hình mặc định của java spring với thymeleaf, nếu
                         * khai báo 1 file ở ngoài templates thì sẽ không chạy được vì không tìm thấy
                         * bên trong folder templates
                         */
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arrJobs);
                    }
                }
            }
        }
    }

    // fix lỗi đa luồng khi sử dụng async (lỗi khi các luồng thread không chia sẻ
    // data cho nhau)
    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();

        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));

        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);

        return res;
    }

}
