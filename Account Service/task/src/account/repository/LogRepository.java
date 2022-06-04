package account.repository;

import account.audit.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface LogRepository extends JpaRepository<EventLog, Long> {

}