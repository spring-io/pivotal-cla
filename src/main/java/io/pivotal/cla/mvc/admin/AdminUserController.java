package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminUserController {
    final UserRepository users;

    public AdminUserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/admin/users/{username}")
    Optional<User> findByUsername(@PathVariable String username) {
        return this.users.findById(username).map((u) -> {
            User result = new User(u);
            result.setAccessToken(null);
            return result;
        });
    }
}
