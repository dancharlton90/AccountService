package account.repository;

import account.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);
    List<Payment> findAllByEmployeeIgnoreCaseOrderByPeriodDesc(String employee);
    Payment findByEmployeeAndPeriod(String email, YearMonth period);
    Payment getByEmployeeIgnoreCaseAndPeriod(String employee, YearMonth period);
}
