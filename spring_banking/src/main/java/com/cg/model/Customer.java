package com.cg.model;


import lombok.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity implements Validator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(precision = 10, columnDefinition = "decimal default 0",updatable = false)
    private BigDecimal balance;

    @Column
    private String address;

    @OneToMany
    private List<Deposit> deposits;

    @OneToMany
    private List<Withdraw> withdraws;

    @OneToMany
    private List<Transfer> senders;

    @OneToMany
    private List<Transfer> recipients;


    @Override
    public boolean supports(Class<?> aClass) {
        return Customer.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer customer = (Customer) target;

        String fullName = customer.fullName;

        if (fullName.length() == 0) {
            errors.rejectValue("fullName", "fullName.empty" );
        }
        else {
            if (fullName.length() < 5 || fullName.length() > 20) {
                errors.rejectValue("fullName", "fullName.length");
            }
        }

        String email = customer.email;
            if (email.length() == 0) {
                errors.rejectValue("email","email.empty");
            }

    }
}
