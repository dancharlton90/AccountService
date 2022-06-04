package account.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.YearMonth;
import java.util.Objects;

@Validated

public class PaymentDto implements Serializable {
    @NotEmpty
    private String employee;
    @JsonFormat(pattern = "MM-yyyy")
    private YearMonth period;
    @Min(value = 0, message = "salary must not be negative!")
    private Long salary;

    public PaymentDto() {
    }

    public PaymentDto(String employee, YearMonth period, Long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public Long getSalary() {
        return salary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDto entity = (PaymentDto) o;
        return Objects.equals(this.employee, entity.employee) &&
                Objects.equals(this.period, entity.period) &&
                Objects.equals(this.salary, entity.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, period, salary);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "employee = " + employee + ", " +
                "period = " + period + ", " +
                "salary = " + salary + ")";
    }
}
