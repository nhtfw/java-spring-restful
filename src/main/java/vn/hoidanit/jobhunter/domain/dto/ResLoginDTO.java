package vn.hoidanit.jobhunter.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    /*
     * JSON có trường "access_token", nó sẽ được ánh xạ tự động vào biến accessToken
     * trong class. Tương tự, khi đối tượng Java này được chuyển thành JSON, biến
     * accessToken sẽ được ghi thành "access_token"
     */
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    // inner class
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }
}
