package account.repository;

import account.user.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByOrderByIdAsc();

    @Transactional
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempts(int newFailAttempts, String email);
}
