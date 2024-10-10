package vn.hoidanit.jobhunter.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;

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

    @Value("${hoidanit.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Autowired
    private JwtEncoder jwtEncoder;

    public String createAccessToken(Authentication authentication, ResLoginDTO.UserLogin dto) {
        // mốc thời gian hiện tại + thời hạn hết hạn của token
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permission
        List<String> listAuthority = new ArrayList<String>();
        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        // tạo payload cho jwt
        // Payload: Chứa dữ liệu về người dùng hoặc các claims (yêu cầu) khác
        /*
         * .issuedAt(now): Thời gian phát hành token.
         * .expiresAt(validity): Thời gian token hết hạn.
         * .subject(authentication.getName()): subject là thông tin định danh người dùng
         * chính (thường là username hoặc ID người dùng).
         * .claim("claim_name", authentication): Bạn thêm một custom claim với tên
         * "claim_name", chứa thông tin xác thực (authentication)
         */
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("user", dto)
                // thêm 1 claim có tên permisson
                .claim("permission", listAuthority)
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

    public String createAccessToken(String email, ResLoginDTO dto) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setName(dto.getUser().getName());

        // mốc thời gian hiện tại + thời hạn hết hạn của token
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permission
        List<String> listAuthority = new ArrayList<String>();
        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        // tạo payload cho jwt
        // Payload: Chứa dữ liệu về người dùng hoặc các claims (yêu cầu) khác
        /*
         * .issuedAt(now): Thời gian phát hành token.
         * .expiresAt(validity): Thời gian token hết hạn.
         * .subject(authentication.getName()): subject là thông tin định danh người dùng
         * chính (thường là username hoặc ID người dùng).
         * .claim("claim_name", authentication): Bạn thêm một custom claim với tên
         * "claim_name", chứa thông tin xác thực (authentication)
         */
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userToken)
                // thêm 1 claim có tên permisson
                .claim("permission", listAuthority)
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

    /*
     * Access Token có thời gian sống ngắn để đảm bảo bảo mật.
     * Refresh Token có thời gian sống dài hơn và được sử dụng để tạo mới Access
     * Token khi hết hạn, giúp duy trì phiên đăng nhập lâu dài mà không cần phải
     * đăng nhập lại thường xuyên.
     * Việc sử dụng cả Access Token và Refresh Token giúp cân bằng giữa bảo mật và
     * trải nghiệm người dùng.
     */
    public String createRefreshToken(String email, ResLoginDTO dto) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setName(dto.getUser().getName());

        // mốc thời gian hiện tại + thời hạn hết hạn của token
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                //
                .claim("user", userToken)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // hàm lấy key, key được lấy từ file môi trường (applications.property)
    private SecretKey getSecretKey() {
        /*
         * jwtKey là một chuỗi đã mã hóa Base64. Phương thức này dùng để giải mã chuỗi
         * này thành mảng byte để tạo ra một đối tượng SecretKey
         * 
         * Base64.from(jwtKey).decode(): Giải mã chuỗi jwtKey từ dạng Base64 thành mảng
         * byte.
         */
        byte[] keyBytes = Base64.from(jwtKey).decode();

        /*
         * SecretKeySpec: Đây là một lớp dùng để tạo đối tượng SecretKey từ mảng byte.
         * Bạn cần đối tượng này để sử dụng làm khóa bí mật cho thuật toán mã hóa của
         * JWT
         * 
         * SecurityUtil.JWT_ALGORITHM.getName() trả về tên của thuật toán mã hóa
         */
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        // lấy ra key
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            // giải mã token, nếu có lỗi trả về lỗi
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh token error: " + e.getMessage());
            throw e;
        }
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
        /*
         * extractPrincipal, Phương thức này (giả định) trích xuất thông tin Principal
         * từ Authentication. Principal thường là username hoặc email của người dùng,
         * tùy thuộc vào cấu hình.
         * 
         * Hàm này chỉ trả về username hoặc email thay vì trả về toàn bộ Authentication
         * để tránh lộ thông tin nhạy cảm như mật khẩu hoặc chi tiết bảo mật không cần
         * thiết khác.
         */
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
}
