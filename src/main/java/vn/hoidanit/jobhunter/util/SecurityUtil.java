package vn.hoidanit.jobhunter.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
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
         * .claim("hoidanit", authentication): Bạn thêm một custom claim với tên
         * "hoidanit", chứa thông tin xác thực (authentication)
         */
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("hoidanit", authentication)
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
}
