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
@Table(name = "transfers")
public class Transfer extends BaseEntity implements Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private Customer sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
    private Customer recipient;

    @Column(name = "transfer_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal transferAmount;

    Long fees;

    @Column(name = "fees_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal feesAmount;

    @Column(name = "transaction_amount", precision = 10, scale = 0, nullable = false)
    private BigDecimal transactionAmount;


    @Override
    public boolean supports(Class<?> clazz) {
        return Transfer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Transfer transfer = (Transfer) target;

        if (transfer.getRecipient().getId() == transfer.getSender().getId()){
            errors.rejectValue("transferAmount","transfer.transferAmount.duplicate");
        }

        BigDecimal transferAmount = transfer.getTransferAmount();
        if (transferAmount == null){
            errors.rejectValue("transferAmount","transfer.transferAmount.null");
            return;
        }

        BigDecimal transactionAmount = transfer.getTransactionAmount();
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.rejectValue("transferAmount","transfer.transferAmount.zero");
        }

        if (transfer.getSender().getBalance().compareTo(transactionAmount) < 0) {
            errors.rejectValue("transferAmount","transfer.transferAmount.exceed");
        }



    }
}
