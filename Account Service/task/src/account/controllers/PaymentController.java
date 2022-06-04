package account.controllers;

import account.payment.PaymentDto;
import account.payment.PaymentService;
import account.user.User;
import account.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    UserService userService;
    @Autowired
    PaymentService paymentService;

    @GetMapping("/empl/payment")
    public ResponseEntity getPayment(@RequestParam(required = false) String period) {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        System.out.println("[DEBUG]PaymentController->getPayment: " + user);
        if (period == null) {
            return paymentService.getAllPaymentsByEmail(user.getEmail());
        } else {
            try {
                YearMonth convertedPeriod = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));
                return paymentService.getPaymentByEmailAndPeriod(user.getEmail(), convertedPeriod);
            } catch (Exception e) {
                System.out.println("Bad Date entered: " + period);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Date Entered");
            }
        }
    }

    @PostMapping("/acct/payments")
    public ResponseEntity addPayments(@RequestBody ArrayList<@Valid PaymentDto> paymentDtos) {
        return paymentService.addPayments(paymentDtos);
    }

    @PutMapping("/acct/payments")
    public ResponseEntity addPaymentToUser(@RequestBody @Valid PaymentDto paymentDto) {
        return paymentService.updatePayment(paymentDto);
    }

}
