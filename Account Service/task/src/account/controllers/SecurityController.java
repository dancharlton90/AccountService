package account.controllers;

import account.audit.LogService;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/security")
@RestController
public class SecurityController {

    @Autowired
    UserService userService;

    @Autowired
    LogService logService;

    @GetMapping("/events")
    public ResponseEntity getEventLogs() {
        return logService.getEventsList();
    }
}
