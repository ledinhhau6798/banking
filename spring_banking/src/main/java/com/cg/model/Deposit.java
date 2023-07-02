package com.cg.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "deposits")
public class Deposit extends BaseEntity {


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

}
