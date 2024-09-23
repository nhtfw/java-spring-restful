package vn.hoidanit.jobhunter.util.error;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.RestResponse;

//controller + RestResponse
@RestControllerAdvice
public class GlobalException {

    /*
     * Khi một ngoại lệ được ném ra từ một phương thức controller, Spring sẽ tìm một
     * phương thức được chú thích với @ExceptionHandler để xử lý ngoại lệ đó.
     */

    // muốn lắng nghe exception nào xảy ra, truyền vào exception đấy
    // @ExceptionHandler(value = IdInvalidException.class)
    @ExceptionHandler(value = {
            IdInvalidException.class,
            UsernameNotFoundException.class,
            //
            BadCredentialsException.class
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(IdInvalidException idInvalidException) {
        RestResponse<Object> res = new RestResponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("IdInvalidException");
        res.setMessage(idInvalidException.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // handle lỗi không nhập username và password (để trống)
    @ExceptionHandler(MethodArgumentNotValidException.class)
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
}
