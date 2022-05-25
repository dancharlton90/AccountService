package account.payment;

import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "payment")
public class Payment {

    @Transient
    @Autowired
    UserService userService;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String employee;

    @NotNull
    private LocalDate period;

    @NotNull
    @Min(0)
    private Long salary;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = userService.getUserByEmail(employee).get();
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

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(String monthYear) {
        this.period = LocalDate.parse(monthYear, DateTimeFormatter.ofPattern("mm-YYYY"));
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return period.equals(payment.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period);
    }
}