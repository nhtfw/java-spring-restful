package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.EmailDuplicateException;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
// chia version
@RequestMapping("/api/v1") // tất cả các url api trong class này sẽ xuất phát với tiền tố "/api/v1"
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
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User reqUser)
            throws EmailDuplicateException {

        boolean isEmailExist = this.userService.isEmailExist(reqUser.getEmail());
        if (isEmailExist) {
            throw new EmailDuplicateException("Email đã được sử dụng");
        }

        String encodedPassword = passwordEncoder.encode(reqUser.getPassword());
        reqUser.setPassword(encodedPassword);
        User user = this.userService.handleCreateUser(reqUser);

        // trả về 1 chuẩn response
        // status : mã (lỗi) phản hồi
        // body : dữ liệu phản hồi, vì generic là User, nên ta trả về user
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    // tự động ép kiểu từ string -> long
    // public String deleteUser(@PathVariable("id") long id) {
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);

        if (user == null) {
            throw new IdInvalidException("Id không tồn tại");
        }

        this.userService.handleDeleteUser(id);

        // return ResponseEntity.status(HttpStatus.OK).body("Delete");
        // status ok(body)
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUser(@PathVariable long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);

        if (user == null) {
            throw new IdInvalidException("Id không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage(value = "fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            /*
             * Để sử dụng spring filter, ta thêm @Filter
             * Trong querry string thêm vào param "filter"
             */
            @Filter Specification<User> spec,
            /*
             * tự động lấy param từ querry string là page và size, page là số trang hiện
             * tại, size là số phần tử của mỗi trang
             * 
             * Có thể sort bằng cách thêm param sort
             */
            Pageable pageable
    // @RequestParam(value = "current", defaultValue = "1") Optional<String>
    // currentOptional,
    // @RequestParam(value = "pageSize", defaultValue = "2") Optional<String>
    // pageSizeOptional
    ) {

        // String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        // String sPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() :
        // "";

        // int current = Integer.parseInt(sCurrent);
        // int pageSize = Integer.parseInt(sPageSize);

        // // lấy từ trang 0 nên trừ 1
        // Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    // cập nhật / patch cập nhật từng trường, put cập nhật cả object
    @PutMapping("/users")
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User newUser)
            throws IdInvalidException {

        User currentUser = this.userService.handleUpdateUser(newUser);
        if (currentUser == null) {
            throw new IdInvalidException("Id không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(currentUser));
    }

}
