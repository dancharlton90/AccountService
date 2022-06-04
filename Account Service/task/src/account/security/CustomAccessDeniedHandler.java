package account.security;

import account.audit.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private LogService logService;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("status", 403);
        data.put("message", "Access Denied!");
        data.put("error", "Forbidden");
        data.put("path", request.getRequestURI());
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));

        String authorization = request.getHeader("Authorization");
        String[] nameAndPass = authorization.split(" ");
        byte[] decodedBytes = Base64.getDecoder().decode(nameAndPass[1]);
        String decodedString = new String(decodedBytes);
        String[] split = decodedString.split(":");
        String email = split[0];
        System.out.println("[DEBUG] LoginFailed: " + email);

        logService.accessDenied(email, request.getRequestURI());
    }
}
