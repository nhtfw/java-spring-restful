package vn.hoidanit.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ReqCreateUserDTO {
    private String name;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    private GenderEnum gender;

    private String address;

    private int age;

    private Company company;

    @Getter
    @Setter
    public static class Company {
        long id;
    }

}
