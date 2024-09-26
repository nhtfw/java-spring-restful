package vn.hoidanit.jobhunter.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

// làm việc với jwt
@Service
public class SecurityUtil {
    // thuật toán mã hóa
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    /*
     * @Value: Annotation này được sử dụng để lấy giá trị từ file cấu hình
     * (application.properties hoặc application.yml).
     */
    @Value("${hoidanit.jwt.base64-secret}")
    private String jwtKey;

    @Value("${hoidanit.jwt.token-validity-in-seconds}")
    private long jwtExpiration;

    @Autowired
    private JwtEncoder jwtEncoder;

    public String createToken(Authentication authentication) {
        // mốc thời gian hiện tại + thời hạn hết hạn của token
        Instant now = Instant.now();
        Instant validity = now.plus(this.jwtExpiration, ChronoUnit.SECONDS);

        // Payload: Chứa dữ liệu về người dùng hoặc các claims (yêu cầu) khác
        // tạo payload cho jwt
        /*
         * .issuedAt(now): Thời gian phát hành token.
         * .expiresAt(validity): Thời gian token hết hạn.
         * .subject(authentication.getName()): subject là thông tin định danh chính
         * (thường là username hoặc ID người dùng).
         * .claim("claim_name", authentication): Bạn thêm một custom claim với tên
         * "claim_name", chứa thông tin xác thực (authentication)
         */
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                //
                .claim("claim_name", authentication)
                .build();

        // Header: Chứa thông tin về loại token và thuật toán mã hóa.
        // tạo header cho jwt
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        /*
         * Phần signature của JWT được thêm vào trong quá trình mã hóa của JwtEncoder.
         * Mặc dù đoạn code bạn chia sẻ không trực tiếp hiển thị việc thêm signature,
         * nhưng nó diễn ra tự động khi JwtEncoder thực hiện mã hóa JWT với thuật toán
         * HMAC-SHA512 (HS512).
         */
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        // lấy tên đăng nhập/email của user đang đăng nhập từ context
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // trả về authentication đã lưu trước đó khi đăng nhập thành công trong context
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        // cú pháp java 14, nếu authentication.getPrincipal() là UserDetails thì gán
        // springSecurityUser = authentication.getPrincipal(), tương tự ở dưới
        else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // return authentication != null
    // &&
    // getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // return (authentication != null && getAuthorities(authentication)
    // .anyMatch(authority -> Arrays.asList(authorities).contains(authority)));
    // }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false
     *         otherwise.
     */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities)
    // {
    // return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    // return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    // return
    // authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }

}
