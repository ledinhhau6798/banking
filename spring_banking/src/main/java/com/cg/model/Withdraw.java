package com.cg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "withdraws")
public class Withdraw extends BaseEntity implements Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @Column(name = "transaction_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal transactionAmount;

    @Override
    public boolean supports(Class<?> clazz) {
        return Withdraw.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Withdraw withdraw = (Withdraw) target;

        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        if (transactionAmount == (null)) {
            errors.rejectValue("transactionAmount", "withdraw.transactionAmount.null");
            return;
        }

        BigDecimal updateBalance = withdraw.customer.getBalance().subtract(transactionAmount);
        BigDecimal minBalance = BigDecimal.valueOf(0L);


        if (transactionAmount.compareTo(BigDecimal.ZERO) < 0){
            errors.rejectValue("transactionAmount","withdraw.transactionAmount.zero");
        }
        if (updateBalance.compareTo(minBalance) < 0) {
            errors.rejectValue("transactionAmount", "withdraw.transactionAmount.min");
        }

    }
}
