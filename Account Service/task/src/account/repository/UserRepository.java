package account.repository;

import account.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
