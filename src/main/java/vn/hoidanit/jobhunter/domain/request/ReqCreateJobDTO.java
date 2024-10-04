package vn.hoidanit.jobhunter.domain.request;

import java.util.List;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ReqCreateJobDTO {

    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Skill[] skills;

    @Getter
    @Setter
    public class Skill {
        private long id;
    }

}
