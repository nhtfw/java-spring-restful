package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController // chuyển thành api
@RequestMapping("/api/v1")
public class AuthController {

        @Autowired
        private AuthenticationManagerBuilder authenticationManagerBuilder;

        @Autowired
        private SecurityUtil securityUtil;

        @Autowired
        private UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
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
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // lưu thông tin vào context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // làm việc với response
                ResLoginDTO res = new ResLoginDTO();

                User currentUSer = this.userService.handleGetUserByUsername(loginDTO.getUsername());
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                currentUSer.getId(),
                                currentUSer.getEmail(),
                                currentUSer.getName());
                res.setUser(userLogin);

                // create a token
                // truyền vào thông tin đăng nhập của người dùng để lấy token
                String accessToken = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
                res.setAccessToken(accessToken);

                // create refresh token and update
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                // create/set cookie
                ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                                /*
                                 * nó chỉ có thể được truy cập qua các yêu cầu HTTP (chẳng hạn như khi gửi yêu
                                 * cầu đến server), và không thể truy cập qua JavaScript. Tăng cường bảo mật,
                                 * tránh các cuộc tấn công XSS
                                 */
                                .httpOnly(true)
                                /*
                                 * Thiết lập cookie chỉ được truyền qua các kết nối an toàn (HTTPS). Cookie sẽ
                                 * không được gửi qua các kết nối HTTP không an toàn
                                 */
                                .secure(true)
                                /*
                                 * Đặt đường dẫn mà cookie này có hiệu lực. Ở đây, đường dẫn là "/", tức là
                                 * cookie sẽ có hiệu lực trên toàn bộ domain và có thể được gửi cùng mọi yêu cầu
                                 * tới các API của server
                                 */
                                .path("/")
                                // thời gian sống của cookie (giây), nếu để = 0, cookie hết ngay lập tức
                                .maxAge(refreshTokenExpiration)
                                /*
                                 * ở đây là "example.com". Cookie sẽ chỉ được gửi đi khi yêu cầu đến từ domain
                                 * "example.com". Nếu người dùng gửi yêu cầu từ domain khác, cookie sẽ không
                                 * được gửi.
                                 */
                                // .domain("example.com")
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {

                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User user = this.userService.handleGetUserByUsername(email);

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

                if (user != null) {
                        userLogin.setId(user.getId());
                        userLogin.setEmail(user.getEmail());
                        userLogin.setName(user.getName());

                        userGetAccount.setUser(userLogin);
                }

                return ResponseEntity.ok().body(userGetAccount);
        }

        /*
         * khi nhận code 401, client (frontend) sẽ tự động gọi API refresh_token, sử
         * dụng token này để đổi lấy {access_token, refresh_token} mới.
         */
        @GetMapping("/auth/refresh")
        @ApiMessage("Get user by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        // lấy ra giá trị của cookie có tên refresh_token
                        @CookieValue(name = "refresh_token") String refresh_token) throws IdInvalidException {

                // check valid refresh_token, nếu không có lỗi trả về JWT
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                // lấy ra subject đã được thêm trước đó khi tạo jwt, dùng để định danh người
                // dùng
                String email = decodedToken.getSubject();

                // check user bằng token và email
                User user = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (user == null) {
                        throw new IdInvalidException("Token không hợp lệ");
                }

                // làm việc với response
                ResLoginDTO res = new ResLoginDTO();

                User currentUSer = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                currentUSer.getId(),
                                currentUSer.getEmail(),
                                currentUSer.getName());
                res.setUser(userLogin);

                // create a token
                // truyền vào thông tin đăng nhập của người dùng để lấy token
                String accessToken = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(accessToken);

                // create refresh token and update
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
                this.userService.updateUserToken(new_refresh_token, email);

                // set lại cookie
                ResponseCookie responseCookie = ResponseCookie.from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(res);
        }

        @GetMapping("/auth/logout")
        @ApiMessage("Logout user")
        public ResponseEntity<Void> logOut() {

                Optional<String> emailOp = this.securityUtil.getCurrentUserLogin();

                String email = emailOp.isPresent() ? emailOp.get() : "";

                this.userService.updateUserToken(null, email);

                // xóa cookie bằng cách cấu hình trùng nhau nhưng maxage = 0;
                ResponseCookie responseCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(null);
        }
}
