package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.hoidanit.jobhunter.domain.RestReponse;

//controller + RestResponse
@RestControllerAdvice
public class GlobalException {

    /*
     * Khi một ngoại lệ được ném ra từ một phương thức controller, Spring sẽ tìm một
     * phương thức được chú thích với @ExceptionHandler để xử lý ngoại lệ đó.
     */

    // muốn lắng nghe exception nào xảy ra, truyền vào exception đấy
    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestReponse<Object>> handleIdException(IdInvalidException idInvalidException) {
        RestReponse<Object> res = new RestReponse<Object>();

        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("IdInvalidException");
        res.setMessage(idInvalidException.getMessage());

        // status.body, class này chạy trước FormatRestResponse
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
