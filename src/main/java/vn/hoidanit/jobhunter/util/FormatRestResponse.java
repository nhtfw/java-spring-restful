package vn.hoidanit.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

// tùy chỉnh/can thiệp phản hồi của api
@ControllerAdvice // generic là object vì chưa biết dữa liệu truyền là gì
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // mặc định tất cả các phản hồi đều được can thiệp/ghi đè
        // vì trả ra true nên sẽ chạy xuống hàm beforebodyWrite ở dưới
        return true;
    }

    // config kết quả trả về
    @Override
    // body là dữ liệu trả về
    public Object beforeBodyWrite(Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // gán ServerHttpResponse sang ServletServerHttpResponse để lấy mã lỗi
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> res = new RestResponse<Object>();

        if (body instanceof String) {
            return body;
        }

        res.setStatusCode(status);

        // config download file
        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        // swagger
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (status >= 400) {
            // // case error
            // res.setError("CALL API FAIL");
            // // message có kiểu dữ liệu là object
            // res.setMessage(body);
            return body;
        } else {
            // case success
            res.setData(body);
            // lấy annotation
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message == null ? "CALL API SUCCESS" : message.value());
        }

        // trả về đối tượng RestResponse và data(body) là một đối tượng nào đó
        return res;
    }

}
