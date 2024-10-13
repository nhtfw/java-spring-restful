package vn.hoidanit.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.error.PermissionException;

// cấu hình phân quyền
// Request => Spring Security => Interceptor => Controller => Service…
// class này chạy trước khi vào handler (controller)
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    /*
     * @Transactional trong Spring Framework được sử dụng để quản lý giao dịch
     * (transaction) tự động. Nó đảm bảo rằng các thao tác với cơ sở dữ liệu được
     * thực hiện trong một giao dịch.
     */
    /*
     * @Transactional : đối với hàm này, đảm bảo khối code ở trong luôn được kết nối
     * tới database
     */
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        // khi nguời dùng đã đăng nhập mới chạy vào đây ( vì email không null)
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);

            if (user != null) {
                Role role = user.getRole();

                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    // quét từng phần tử một, check xem có phần tử nào matching với tiêu chí đề ra
                    // hay không
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path)
                                    &&
                                    item.getMethod().equals(httpMethod));

                    if (!isAllow) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                    }

                } else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này");
                }
            }
        }

        // nếu trả ra true,request được đi tiếp và ngược lại
        return true;
    }
}
