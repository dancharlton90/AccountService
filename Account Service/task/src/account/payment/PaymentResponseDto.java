package account.payment;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PaymentResponseDto implements Serializable {
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String lastname;
    @JsonFormat(pattern = "MM-yyyy")
    private final YearMonth period;
    @Min(value = 0, message = "salary must not be negative!")
    private final Long salary;

    public PaymentResponseDto(String name, String lastname, YearMonth period, Long salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPeriod() {
        return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy"));
    }

    public String getSalary() {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentResponseDto entity = (PaymentResponseDto) o;
        return Objects.equals(this.period, entity.period) &&
                Objects.equals(this.salary, entity.salary) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.lastname, entity.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, salary, name, lastname);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "period = " + period + ", " +
                "salary = " + salary + ", " +
                "name = " + name + ", " +
                "lastname = " + lastname + ")";
    }
}
