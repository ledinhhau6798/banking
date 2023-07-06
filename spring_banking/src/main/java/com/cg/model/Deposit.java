package com.cg.model;

import lombok.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "deposits")
public class Deposit extends BaseEntity implements Validator {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;


    @Column(name = "transaction_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal transactionAmount;


    @Override
    public String toString() {
        return "Deposit{" +
                "id=" + id +
                ", transactionAmount=" + transactionAmount +
                '}';
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Deposit.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Deposit deposit = (Deposit) target;


        BigDecimal transactionAmount = deposit.getTransactionAmount();
        if (transactionAmount == (null) || transactionAmount.equals("") ){
            errors.rejectValue("transactionAmount", "deposit.transactionAmount.null");
            return;
        }

        BigDecimal maxTransactionAmount = BigDecimal.valueOf(999999999L);

        BigDecimal maxBalanceAmount = BigDecimal.valueOf(1000000000L);
        BigDecimal updateBalance = deposit.customer.getBalance().add(transactionAmount);



        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("transactionAmount", "deposit.transactionAmount.zero");
        }

        if (transactionAmount.compareTo(maxTransactionAmount) > 0) {
            errors.rejectValue("transactionAmount", "deposit.transactionAmount.max");
        }

        if (updateBalance.compareTo(maxBalanceAmount) >= 0) {
             errors.rejectValue("transactionAmount", "deposit.maxTotalAmount");
        }

    }
}
