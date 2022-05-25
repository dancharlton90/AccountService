package account.user;

import account.exceptions.BadPasswordException;
import account.exceptions.PasswordCompromisedException;
import account.exceptions.SamePasswordGivenException;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Set<String> compromisedPasswords = new HashSet<>(Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
            "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

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
            save(user);
        }
    }
}
