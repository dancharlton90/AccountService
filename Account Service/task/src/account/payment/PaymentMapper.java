package account.payment;

import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    @Autowired
    private UserService userService;

    public PaymentMapper(UserService userService) {
        this.userService = userService;
    }

    public Payment mapToEntity(PaymentDto dto) {
        return new Payment(
                dto.getEmployee(),
                dto.getPeriod(),
                dto.getSalary(),
                userService.getUserByEmail(dto.getEmployee()).get()
        );
    }

    public PaymentResponseDto mapToDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getUser().getName(),
                payment.getUser().getLastname(),
                payment.getPeriod(),
                payment.getSalary()
        );
    }
}
