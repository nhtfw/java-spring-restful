package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    };

    public User handleUpdateUser(User newUser) {
        User currentUser = this.fetchUserById(newUser.getId());
        // vì đã có id nên thay vì create, chương trình sẽ update
        if (currentUser != null) {

            currentUser.setName(newUser.getName());
            currentUser.setEmail(newUser.getEmail());
            currentUser.setPassword(newUser.getPassword());

            currentUser = this.userRepository.save(currentUser);
        }

        return currentUser;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
