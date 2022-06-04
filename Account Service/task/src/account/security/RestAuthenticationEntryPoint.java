package account.security;

import account.audit.LogService;
import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String[] nameAndPass = authorization.split(" ");
            byte[] decodedBytes = Base64.getDecoder().decode(nameAndPass[1]);
            String decodedString = new String(decodedBytes);
            String[] split = decodedString.split(":");
            String email = split[0];
            System.out.println("[DEBUG] LoginFailed: " + email);

            User user = userService.getUserByEmail(email).orElse(null);

            System.out.println("[DEBUG] LoginFailed User: " + user);

            if (user != null) {
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    if (user.getFailedAttempts() < UserService.MAX_FAILED_ATTEMPTS) {
                        logService.loginFailed(user.getEmail(), request.getRequestURI());
                        user.incrementFailedAttempt();
                        userService.save(user);
                        System.out.println("[DEBUG]Increment: " + user.getFailedAttempts());
                    } else {
                        userService.lock(user);
                        logService.loginFailed(user.getEmail(), request.getRequestURI());
                        logService.bruteForceAttemptAndLock(user.getEmail(), request.getRequestURI());
                        authException = new LockedException("Your account has been locked due to 5 failed attempts.");
                    }
                }
            } else {
                logService.loginFailed(email, request.getRequestURI());
            }
//            if (user != null) {
//                if (user.getFailedAttempts() > 0) {
//                    userService.resetFailedAttempts(user);
//                }
//            }
        }


        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

}
