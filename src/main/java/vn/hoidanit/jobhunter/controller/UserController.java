package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {

    /*
     * các method CRUD này đã có Spring Data REST làm thay nhưng ở đây tôi không
     * dùng
     */

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    // RequestBody sẽ tự động mapping với JSON dựa vào biến (field) của JSON và đối
    // tượng User truyền vào

    /*
     * @RequestBody được sử dụng để ánh xạ dữ liệu từ body của request HTTP vào một
     * đối tượng Java.
     * Thường được sử dụng với JSON và XML.
     * Hỗ trợ các kiểu dữ liệu phức tạp như List, Map, và có thể kết hợp với @Valid
     * để kiểm tra tính hợp lệ của dữ liệu đầu vào.
     */

    /*
     * nói dễ hiểu là để lấy dữ liệu của client truyền lên sv trước đó, cần tới
     * RequestBody
     */
    public ResponseEntity<User> createUser(@RequestBody User postmanUser) {

        String encodedPassword = passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(encodedPassword);
        User user = this.userService.handleCreateUser(postmanUser);

        // trả về 1 chuẩn response
        // status : mã (lỗi) phản hồi
        // body : dữ liệu phản hồi, vì generic là User, nên ta trả về user
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/users/{id}")
    // tự động ép kiểu từ string -> long
    // public String deleteUser(@PathVariable("id") long id) {
    public ResponseEntity<String> deleteUser(@PathVariable long id) throws IdInvalidException {
        if (id > 1500) {
            // khi chương trình chạy vào đây, chương trình sẽ chạy tiếp vào
            // handleIdException ở trong GlobalException, và tham số của handleIdException
            // sẽ là
            // IdInvalidException ở trong này
            throw new IdInvalidException("Khong lon hon 1500");
        }

        this.userService.handleDeleteUser(id);

        // return ResponseEntity.status(HttpStatus.OK).body("Delete");
        // status ok(body)
        return ResponseEntity.ok("delete");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUser(@PathVariable long id) {
        User user = this.userService.fetchUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {

        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";

        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSize);

        // lấy từ trang 0 nên trừ 1
        Pageable pageable = PageRequest.of(current - 1, pageSize);

        // List<User> users = this.userService.fetchAllUser(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(pageable));
    }

    // cập nhật / patch cập nhật từng trường, put cập nhật cả object
    @PutMapping("/users")
    public ResponseEntity<User> putMethodName(@RequestBody User newUser) {

        User currentUser = this.userService.handleUpdateUser(newUser);

        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
    }

}
