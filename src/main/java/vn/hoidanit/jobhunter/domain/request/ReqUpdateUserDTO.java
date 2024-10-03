package vn.hoidanit.jobhunter.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ReqUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private int age;
    private String address;
    private Company company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Company {
        long id;
    }
}
