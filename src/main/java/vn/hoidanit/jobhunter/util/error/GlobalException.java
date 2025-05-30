package vn.hoidanit.jobhunter.util.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

//controller + RestResponse
@RestControllerAdvice
public class GlobalException {

    // handle all exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    /*
     * Khi một ngoại lệ được ném ra từ một phương thức controller, Spring sẽ tìm một
     * phương thức được chú thích với @ExceptionHandler để xử lý ngoại lệ đó.
     */

    // muốn lắng nghe exception nào xảy ra, truyền vào exception đấy
    // @ExceptionHandler(value = IdInvalidException.class)
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            // nếu BadCredentialsException không chạy vào đây, có thể bạn chưa cấu hình jwt
            // lỗi thông tin đăng nhập sai (username/password)
            BadCredentialsException.class,
            IdInvalidException.class
    })
    // vì handle 2 exception khác nhau, nên dùng class cha của 2 ex ở trên là
    // Exception
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception occurs ...");
        res.setMessage(ex.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // handle lỗi không nhập username và password (để trống)
    // handle lỗi validate dữ liệu (dữ liệu để trống,...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // truyền generic là object vì data == null
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        // lấy message lỗi
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        // lấy message của từng lỗi (tự làm)
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(fieldError.getDefaultMessage());
        }
        // List<String> errors = fieldErrors.stream().map(f ->
        // f.getDefaultMessage()).collect(Collectors.toList());

        // nếu size của errors lớn hơn 1 (tức nhiều hơn 1 lỗi), trả về array
        // ngược lại trả về phần tử là lỗi đầu tiên
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<RestResponse<Object>> emailDuplicateException(EmailDuplicateException ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Email duplicate");
        res.setMessage(ex.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> noResourceException(NoResourceFoundException ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Đường link không tồn tại");
        res.setMessage(ex.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<RestResponse<Object>> nullPointException(NullPointerException ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Filter sai cú pháp");
        res.setMessage(ex.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<RestResponse<Object>> uploadFileException(StorageException ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception upload file");
        res.setMessage(ex.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<RestResponse<Object>> permissionException(PermissionException ex) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Forbidden");
        res.setMessage(ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

}
