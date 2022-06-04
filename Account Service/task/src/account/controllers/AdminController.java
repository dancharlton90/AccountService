package account.controllers;

import account.user.User;
import account.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @Autowired
    UserService userService;

    @GetMapping("/user")
    public ResponseEntity getUserInfo() {
        return userService.getUserList();
    }

    @PutMapping("/user/role")
    public ResponseEntity setUserRole(@RequestBody JsonNode jsonNode) {
        User adminUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        return userService.changeRole(
                adminUser,
                jsonNode.get("user").asText(),
                jsonNode.get("role").asText(),
                jsonNode.get("operation").asText()
        );
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity deleteUser(@PathVariable String email) {
        User adminUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        return userService.deleteUserByEmail(adminUser, email);
    }

    @PutMapping("/user/access")
    public ResponseEntity setAccess(@RequestBody JsonNode jsonNode) {
        User adminUser = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        return userService.setAccountLock(
                adminUser,
                jsonNode.get("user").asText(),
                jsonNode.get("operation").asText()
        );
    }
}
