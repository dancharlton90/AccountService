package account.audit;

import account.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class LogService {

    @Autowired
    LogRepository logRepository;


    public ResponseEntity getEventsList() {
        return ResponseEntity.ok(logRepository.findAll());
    }

    public void createUser(String email) {
        EventLog eventLog = new EventLog(
                "CREATE_USER", "Anonymous", email, "/api/auth/signup");
        logRepository.save(eventLog);
    }

    public void changePassword(String email) {
        EventLog eventLog = new EventLog("CHANGE_PASSWORD", email, email, "/api/auth/changepass");
        logRepository.save(eventLog);
    }

    public void accessDenied(String subjectEmail, String path) {
        EventLog eventLog = new EventLog("ACCESS_DENIED", subjectEmail, path, path);
        logRepository.save(eventLog);
    }

    public void loginFailed(String subjectEmail, String path) {
        EventLog eventLog = new EventLog("LOGIN_FAILED", subjectEmail, path, path);
        logRepository.save(eventLog);
    }

    public void grantRole(String subjectEmail, String objectEmail, String role) {
        EventLog eventLog = new EventLog(
                "GRANT_ROLE",
                subjectEmail,
                "Grant role " + role + " to " + objectEmail.toLowerCase(),
                "/api/admin/user/role");
        logRepository.save(eventLog);
    }

    public void removeRole(String subjectEmail, String objectEmail, String role) {
        EventLog eventLog = new EventLog(
                "REMOVE_ROLE",
                subjectEmail,
                "Remove role " + role + " from " + objectEmail.toLowerCase(),
                "/api/admin/user/role");
        logRepository.save(eventLog);
    }

    public void lockUserManual(String subjectEmail, String objectEmail) {
        EventLog eventLog = new EventLog("LOCK_USER", subjectEmail, "Lock user " + objectEmail, "/api/admin/user/access");
        logRepository.save(eventLog);
    }

    public void unlockUser(String subjectEmail, String objectEmail) {
        EventLog eventLog = new EventLog("UNLOCK_USER", subjectEmail, "Unlock user " + objectEmail, "/api/admin/user/access");
        logRepository.save(eventLog);
    }

    public void deleteUser(String subjectEmail, String objectEmail) {
        EventLog eventLog = new EventLog("DELETE_USER", subjectEmail, objectEmail, "/api/admin/user");
        logRepository.save(eventLog);
    }

    public void bruteForceAttemptAndLock(String subjectEmail, String path) {
        EventLog eventLog = new EventLog("BRUTE_FORCE", subjectEmail, path, path);
        logRepository.save(eventLog);
        lockUserAuto(subjectEmail, path);
    }

    private void lockUserAuto(String subjectEmail, String path) {
        EventLog eventLog = new EventLog("LOCK_USER", subjectEmail, "Lock user " + subjectEmail, path);
        logRepository.save(eventLog);
    }
}
