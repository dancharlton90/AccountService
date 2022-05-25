package account.controllers;

import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empl")
public class PaymentController {

    @Autowired
    UserService userService;

    @GetMapping("/payment")
    public ResponseEntity getPayment(@AuthenticationPrincipal User user) {
        if (userService.userExistsByEmail(user.getUsername())) {
            return ResponseEntity.ok(userService.getUserByEmail(user.getUsername()).get());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

}
