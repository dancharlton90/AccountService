package account.user;

import account.audit.EventLog;
import account.audit.LogService;
import account.exceptions.BadPasswordException;
import account.exceptions.PasswordCompromisedException;
import account.exceptions.SamePasswordGivenException;
import account.exceptions.UserExistException;
import account.repository.UserRepository;
import account.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    LogService logService;

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    PasswordEncoder encoder;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    Set<String> compromisedPasswords = new HashSet<>(Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
            "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempts() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    public void resetFailedAttempts(User user) {
        userRepository.updateFailedAttempts(0, user.getEmail());
    }

    public void lock(User user) {
            user.setAccountNonLocked(false);
            user.setFailedAttempts(0);
            userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException(String.format("Username [%email] not found"));
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public void changePassword(User user, String newPassword) {
        if (newPassword.length() < 12) {
            throw new BadPasswordException();
        } else if (compromisedPasswords.contains(newPassword)) {
            throw new PasswordCompromisedException();
        } else if (encoder.matches(newPassword, user.getPassword())) {
            throw new SamePasswordGivenException();
        } else {
            user.setPassword(encoder.encode(newPassword));
            logService.changePassword(user.getEmail());
            save(user);
        }
    }

    public ResponseEntity createUser(User user) {
        if (userExistsByEmail(user.getEmail())) {
            throw new UserExistException();
        } else if (compromisedPasswords.contains(user.getPassword())) {
            throw new PasswordCompromisedException();
        } else {
            if (userRepository.findAll().isEmpty()) {
                user.setPassword(encoder.encode(user.getPassword()));
                user.grantAuthority(Role.ROLE_ADMINISTRATOR);
                logService.createUser(user.getEmail());
                save(user);
            } else {
                user.setPassword(encoder.encode(user.getPassword()));
                user.grantAuthority(Role.ROLE_USER);
                logService.createUser(user.getEmail());
                save(user);
            }
            return ResponseEntity.ok(userMapper.mapToDto(user));
        }
    }

    public ResponseEntity getUserList() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepository.findAllByOrderByIdAsc()) {
            userDtoList.add(userMapper.mapToDto(user));
        }
        return ResponseEntity.ok(userDtoList);
    }


    public ResponseEntity changeRole(User adminUser, String userEmail, String role, String operation) {
        if (!userExistsByEmail(userEmail)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        String longRole = "ROLE_" + role.toUpperCase();
        if (!isInRoleEnum(longRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
        User user = getUserByEmail(userEmail).get();
        switch (operation) {
            case "GRANT":
                try {
                    user = grantAccess(user, longRole);
                    save(user);
                    logService.grantRole(adminUser.getEmail(), user.getEmail(), role.toUpperCase());
                    return ResponseEntity.ok(userMapper.mapToDto(user));
                } catch (ResponseStatusException e) {
                    throw e;
                }
            case "REMOVE":
                try {
                    user = removeAccess(user, longRole);
                    save(user);
                    logService.removeRole(adminUser.getEmail(), userEmail, role.toUpperCase());
                    return ResponseEntity.ok(userMapper.mapToDto(user));
                } catch (ResponseStatusException e) {
                    throw e;
                }
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Not a valid operation: Must be \"GRANT\" or \"REMOVE\"");
        }


    }

    private User removeAccess(User user, String role) {
        switch (role) {

            case "ROLE_ADMINISTRATOR":
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Can't remove ADMINISTRATOR role!");

            case "ROLE_ACCOUNTANT":
                if (!user.getRoles().contains(Role.ROLE_ACCOUNTANT)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user does not have a role!");
                } else if (user.getRoles().size() <= 1) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user must have at least one role!");
                } else {
                    user.removeRole(Role.ROLE_ACCOUNTANT);
                    return user;
                }

            case "ROLE_USER":
                if (!user.getRoles().contains(Role.ROLE_USER)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user does not have a role!");
                } else if (user.getRoles().size() <= 1) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user must have at least one role!");
                } else {
                    user.removeRole(Role.ROLE_USER);
                    return user;
                }

            case "ROLE_AUDITOR":
                if (!user.getRoles().contains(Role.ROLE_AUDITOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user does not have a role!");
                } else if (user.getRoles().size() <= 1) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user must have at least one role!");
                } else {
                    user.removeRole(Role.ROLE_AUDITOR);
                    return user;
                }

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Remove Access hit default switch condition?");
        }
    }

    private User grantAccess(User user, String role) {
        switch (role) {

            case "ROLE_ADMINISTRATOR":
                if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user already has this role!");
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user cannot combine administrative and business roles!");
                }

            case "ROLE_ACCOUNTANT":
                if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user cannot combine administrative and business roles!");
                } else if (user.getRoles().contains(Role.ROLE_ACCOUNTANT)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user already has this role!");
                } else {
                    user.grantAuthority(Role.ROLE_ACCOUNTANT);
                    return user;
                }

            case "ROLE_USER":
                if (user.getRoles().contains(Role.ROLE_USER)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user already has this role!");
                } else if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user cannot combine administrative and business roles!");
                } else {
                    user.grantAuthority(Role.ROLE_USER);
                    return user;
                }

            case "ROLE_AUDITOR":
                if (user.getRoles().contains(Role.ROLE_AUDITOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user already has this role!");
                } else if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "The user cannot combine administrative and business roles!");
                } else {
                    user.grantAuthority(Role.ROLE_AUDITOR);
                    return user;
                }

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Grant Access hit default switch condition?");
        }
    }



    private boolean isInRoleEnum(String value) {
        return Arrays.stream(Role.class.getEnumConstants()).anyMatch(e -> e.name().equals(value));
    }

    public ResponseEntity deleteUserByEmail(User adminUser, String email) {
        if (!userExistsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else {
            User user = getUserByEmail(email).get();
            System.out.println("========[DEBUG] DELETE ADMIN==========" + user.getAuthorities().toString());
            if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            } else {
                logService.deleteUser(adminUser.getEmail(), user.getEmail());
                userRepository.delete(user);

                return ResponseEntity.ok(new HashMap<>(Map.of(
                        "user", user.getEmail(), "status", "Deleted successfully!")));
            }
        }
    }

    public ResponseEntity setAccountLock(User adminUser, String userEmail, String operation) {
        if (!userExistsByEmail(userEmail)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        if (!operation.equalsIgnoreCase("LOCK") && !operation.equalsIgnoreCase("UNLOCK")) {
            System.out.println("[DEBUG]SetAccountLog operation: " + operation);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid operation: Must be \"LOCK\" or \"UNLOCK\"");
        }

        User user = getUserByEmail(userEmail).get();

        switch (operation) {
            case "LOCK":
                if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Can't lock the ADMINISTRATOR!");
                } else if (user.isAccountNonLocked() == false) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "User " + userEmail.toLowerCase() + " is already locked!");
                } else {
                    lock(user);
                    logService.lockUserManual(adminUser.getEmail(), user.getEmail());
                    return ResponseEntity.ok(new HashMap<>(Map.of(
                            "status", "User " + userEmail.toLowerCase() + " locked!")));
                }

            case "UNLOCK":
                if (user.isAccountNonLocked() == true) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "User " + userEmail.toLowerCase() + " is already unlocked!");
                } else {
                    user.setAccountNonLocked(true);
                    user.setFailedAttempts(0);
                    save(user);
                    logService.unlockUser(adminUser.getEmail(), user.getEmail());
                    return ResponseEntity.ok(new HashMap<>(Map.of(
                            "status", "User " + userEmail.toLowerCase() + " unlocked!")));
                }

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "setAccountLock hit default switch condition?");
        }
    }
}
