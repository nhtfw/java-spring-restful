package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController // chuyển thành api
public class AuthController {

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        /*
         * 
         * Dòng này thực hiện việc xác thực thông qua authenticationManagerBuilder.
         * authenticationManagerBuilder sẽ kiểm tra thông tin đăng nhập và gọi đến
         * UserDetailsService (hoặc loadUserByUsername()) để nạp thông tin người dùng từ
         * database.
         * 
         * Nếu thông tin là hợp lệ, quá trình xác thực thành công và một đối tượng
         * Authentication chứa thông tin xác thực sẽ được trả về.
         * 
         */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // create a token
        // truyền vào thông tin đăng nhập của người dùng để lấy token
        String accessToken = this.securityUtil.createToken(authentication);

        // lưu thông tin vào context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        res.setAccessToken(accessToken);

        return ResponseEntity.ok().body(res);
    }
}
