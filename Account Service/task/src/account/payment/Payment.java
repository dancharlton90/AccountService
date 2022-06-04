package account.payment;

import account.user.User;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.YearMonth;
import java.util.Objects;

@Validated
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"period", "employee"}))
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty
    private String employee;

    private YearMonth period;

    @Min(value = 0, message = "salary must not be negative!")
    private Long salary;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    public Payment() {
    }

    public Payment(String employee, YearMonth period, Long salary, User user) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return employee.equals(payment.employee) && period.equals(payment.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period);
    }
}
