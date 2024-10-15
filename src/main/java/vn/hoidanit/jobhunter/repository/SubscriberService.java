package vn.hoidanit.jobhunter.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SkillRepository skillRepository;

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

}
