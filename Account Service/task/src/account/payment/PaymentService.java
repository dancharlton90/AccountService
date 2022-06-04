package account.payment;

import account.repository.PaymentRepository;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
    }

    @Transactional
    public ResponseEntity addPayments(List<PaymentDto> paymentDtos) {
        System.out.println("[DEBUG]PaymentService->addPayments called");
        System.out.println("[DEBUG]PaymentService->addPayments size: " + paymentDtos.size());
        for (PaymentDto dto : paymentDtos) {
            Payment payment = paymentMapper.mapToEntity(dto);
            if (userRepository.findByEmailIgnoreCase(payment.getEmployee()).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee: " + payment.getEmployee() + " not found");
            }
            if (paymentRepository.existsByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Record already exists for: " + payment.getEmployee() + " with date " + payment.getPeriod());
            }
            if (payment.getSalary() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary must not be negative!");
            }
            System.out.println("[DEBUG]PaymentService->addPayments: " + payment);
            paymentRepository.save(payment);
        }
        return new ResponseEntity(new HashMap<>(Map.of("status", "Added successfully!")), HttpStatus.OK);
    }

    public ResponseEntity updatePayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.mapToEntity(paymentDto);
        if (userRepository.findByEmailIgnoreCase(payment.getEmployee()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee: " + payment.getEmployee() + " not found");
        }
        if (paymentRepository.existsByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod())) {
            Payment paymentToUpdate = paymentRepository.getByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(), payment.getPeriod());
            paymentToUpdate.setSalary(payment.getSalary());
            paymentRepository.save(paymentToUpdate);
        } else {
            paymentRepository.save(payment);
        }
        return new ResponseEntity(new HashMap<>(Map.of("status", "Updated successfully!")), HttpStatus.OK);
    }

    public ResponseEntity getAllPaymentsByEmail(String email) {
        System.out.println("[DEBUG]Email: " + email);
        List<Payment> paymentList = paymentRepository.findAllByEmployeeIgnoreCaseOrderByPeriodDesc(email);
        System.out.println("[DEBUG]PaymentService->getAllPaymentsByEmail(fromDB): " + paymentList);
        List<PaymentResponseDto> responseDtos = new ArrayList<>();
        for (Payment payment : paymentList) {
            responseDtos.add(paymentMapper.mapToDto(payment));
        }
        System.out.println("[DEBUG]PaymentService->getAllPaymentsByEmail: " + responseDtos);
        return new ResponseEntity(responseDtos, HttpStatus.OK);
    }

    public ResponseEntity getPaymentByEmailAndPeriod(String email, YearMonth period) {
        PaymentResponseDto responseDto = paymentMapper.mapToDto(paymentRepository.findByEmployeeAndPeriod(email, period));
        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
