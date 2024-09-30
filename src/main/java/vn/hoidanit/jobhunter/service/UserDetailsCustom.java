package vn.hoidanit.jobhunter.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userDetailsService") // ghi đè lại UserDetailsService, vì quy tắc đặt tên của bean nên chữ cái đầu là
                                 // viết thường
public class UserDetailsCustom implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    // query user trong database bằng tham số username truyền vào này
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.hoidanit.jobhunter.domain.User user = userService.handleGetUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username/Password không hợp lệ");
        }

        // nếu muốn lưu thêm data của user như avatar,... thì phải kế thừa UserDetails
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
