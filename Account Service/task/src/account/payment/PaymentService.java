package account.payment;

import account.repository.PaymentRepository;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentService {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentService(UserService userService, PaymentRepository paymentRepository) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
    }


}
