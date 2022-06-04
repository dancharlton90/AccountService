package account.controllers;

import account.audit.LogService;
import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserService userService;


    Set<String> compromisedPasswords = new HashSet<>(Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
            "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));


    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @PostMapping("/changepass")
    public ResponseEntity changePassword(@RequestBody Map<String, String> passwordMap) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        String newPassword = passwordMap.get("new_password");
        userService.changePassword(user, newPassword);
        return new ResponseEntity(new HashMap<>(Map.of("email", user.getEmail(),
                    "status", "The password has been updated successfully")), HttpStatus.OK);
    }
}