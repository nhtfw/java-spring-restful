package vn.hoidanit.jobhunter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.RestResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/* Khi một yêu cầu không có token hoặc token không hợp lệ, 
Spring sẽ gọi AuthenticationEntryPoint để xử lý phản hồi cho client */
//handle lỗi 401, truyền sai token
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    // hỗ trợ chuyển data -> object
    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // để chương trình chạy mặc định trước, mặc định sẽ không trả về body
        this.delegate.commence(request, response, authException);
        // set kiểu data dưới dạng UTF-8, hỗ trợ tiếng Việt
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        // lấy message lỗi không truyền token + lỗi token sai
        String errorMessage = Optional.ofNullable(authException.getCause()) // kiểm tra authException.getCause()
                .map(Throwable::getMessage) // nếu authException.getCause() khác null
                .orElse(authException.getMessage()); // nếu authException.getCause() == null

        res.setError(errorMessage);

        res.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...");

        // truyền lại data cho phía client
        mapper.writeValue(response.getWriter(), res);
    }
}