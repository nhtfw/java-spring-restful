package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import vn.hoidanit.jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    // not use
    User findTop1ById(long id);

    List<User> findAll();

    User findByEmail(String email);
}
