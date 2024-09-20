package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {

    /*
     * các method CRUD này đã có Spring Data REST làm thay nhưng ở đây tôi không
     * dùng
     */

    @Autowired
    private UserService userService;

    @PostMapping("/user")
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
    public User createUser(@RequestBody User postmanUser) {

        User user = this.userService.handleCreateUser(postmanUser);

        // chuyển object -> Json
        return user;
    }

    @DeleteMapping("/user/{id}")
    // tự động ép kiểu từ string -> long
    // public String deleteUser(@PathVariable("id") long id) {
    public String deleteUser(@PathVariable long id) {

        this.userService.handleDeleteUser(id);

        return "delete";
    }

    @GetMapping("/user/{id}")
    public User fetchUser(@PathVariable long id) {
        User user = this.userService.fetchUserById(id);
        if (user != null) {
            return user;
        }
        return null;
    }

    @GetMapping("/user")
    public List<User> fetchAllUser() {

        List<User> users = this.userService.fetchAllUser();

        return users;
    }

    // cập nhật / patch cập nhật từng trường, put cập nhật cả object
    @PutMapping("/user")
    public User putMethodName(@RequestBody User newUser) {

        User currentUser = this.userService.fetchUserById(newUser.getId());

        return currentUser;
    }

}
