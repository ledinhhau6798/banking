package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.service.customer.ICustomerService;
import com.cg.service.deposit.IDepositService;
import com.cg.service.transfer.ITransferService;
import com.cg.service.withdraw.IWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;
    @Autowired
    private ITransferService transferService;
    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private IDepositService depositService;


    @GetMapping
    public String showListCustomer(Model model) {
        List<Customer> customers = customerService.findAllByDeletedIsFalse();
        model.addAttribute("customers", customers);

        return "/customer/list";
    }

    @GetMapping("/create")
    public String showCreate(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "/customer/create";
    }

    @PostMapping("/create")
    public String doCreate(@ModelAttribute Customer customer, BindingResult bindingResult, Model model) {

        new Customer().validate(customer, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            model.addAttribute("hasError", true);
            return "/customer/create";
        }


        String email = customer.getEmail();
        Boolean existsEmail = customerService.existsByEmail(email);

        if (existsEmail) {
            model.addAttribute("notValid", true);
            model.addAttribute("message", "Email đã tồn tại");
            return "/customer/create";
        }

        customer.setBalance(BigDecimal.ZERO);
        customerService.save(customer);

        model.addAttribute("customer", new Customer());
        model.addAttribute("success", true);
        model.addAttribute("messages", "thêm thành công");

        return "/customer/create";
    }

    @GetMapping("/edit/{id}")
    public String showUpdate(@PathVariable String id, Model model) {
        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customerOptional = customerService.findById(customerId);

            if (customerOptional.isEmpty()) {
                return "redirect:/errors/404";
            }

            Customer customer = customerOptional.get();

            model.addAttribute("customer", customer);

            return "/customer/edit";
        } catch (Exception e) {
            return "/errors/404";
        }
    }

    @PostMapping("/edit/{id}")
    public String toUpdate(@PathVariable Long id, Model model, @ModelAttribute Customer customer,BindingResult bindingResult) {
        Optional<Customer> customerOptional = customerService.findById(id);

        if (bindingResult.hasFieldErrors()) {
            model.addAttribute("hasError", true);
            return "/customer/edit";
        }


        String email = customer.getEmail();
        Boolean existsEmail = customerService.existsByEmail(email);

        if (existsEmail) {
            model.addAttribute("notValid", true);
            model.addAttribute("message", "Email đã tồn tại");
            return "/customer/edit";
        }

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
        } else {
            customer.setId(id);
            customer.setBalance(customerOptional.get().getBalance());
            customerService.save(customer);
            model.addAttribute("customer", customer);
            model.addAttribute("success", true);
            model.addAttribute("messages", "sửa thành công");
        }


//        List<Customer> customers = customerService.findAll();


        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            Optional<Customer> customerOptional = customerService.findById(id);
            Customer customer = customerOptional.get();
            customer.setDeleted(true);
            customerService.save(customer);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Xóa thành công");

            return "redirect:/customers";
        } catch (Exception e) {
            return "/errors/404";
        }
    }

    @GetMapping("/deposit/{id}")
    public String showDeposit(@PathVariable String  id, Model model) {



        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customerOptional = customerService.findById(customerId);

            if (customerOptional.isEmpty()) {
                return "redirect:/errors/404";
            }

            Customer customer = customerOptional.get();

            Deposit deposit = new Deposit();
            deposit.setCustomer(customer);

            model.addAttribute("deposit", deposit);

            return "/customer/deposit";
        } catch (Exception e) {
            return "/errors/404";
        }
    }

    @PostMapping("/deposit/{customerId}")
    public String doDeposit(@PathVariable Long customerId, Model model, @ModelAttribute Deposit deposit, BindingResult bindingResult) {

        new Deposit().validate(deposit, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return "/customer/deposit";
        }

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            model.addAttribute("error", true);
            model.addAttribute("message", "ID khách hàng không tồn tại");
        } else {
            try {
                Customer customer = customerService.deposit(deposit);

                deposit.setCustomer(customer);

                model.addAttribute("deposit", deposit);
            } catch (Exception ex) {
                return "/errors/500";
            }
        }

        return "/customer/deposit";
    }

    @GetMapping("/withdraw/{id}")
    public String showWithdraw(@PathVariable Long id, Model model) {


        Optional<Customer> customerOptional = customerService.findById(id);

        if (!customerOptional.isPresent()){
            model.addAttribute("error",true);
            model.addAttribute("message","Id khách hàng không tồn tại");
        } else {
            Customer customer = customerOptional.get();
            Withdraw withdraw = new Withdraw();
            withdraw.setCustomer(customer);

            model.addAttribute("withdraw",withdraw);
        }
        return "/customer/withdraw";


    }

    @PostMapping("/withdraw/{customerId}")
    public String toWithdraw(@PathVariable Long customerId,Model model,@ModelAttribute Withdraw withdraw, BindingResult bindingResult ) {
        new Withdraw().validate(withdraw,bindingResult);
        if (bindingResult.hasFieldErrors()){
            return "/customer/withdraw";
        }
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()){
            model.addAttribute("error",true);
            model.addAttribute("message","Id không tồn tại");
        }else {
            try {
                Customer customer = customerService.withdraw(withdraw);
                withdraw.setCustomer(customer);

                model.addAttribute("withdraw",withdraw);
            }catch (Exception e){
                return "/errors/500";
            }
        }
        return "/customer/withdraw";

    }

    @GetMapping("/transfer/{customerId}")
    public String showTransfer(@PathVariable Long customerId, Model model) {
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (!customerOptional.isPresent()){
            model.addAttribute("error",true);
            model.addAttribute("message","Id khách hàng không tồn tại");
        } else {
            Customer customer = customerOptional.get();
            Transfer transfer = new Transfer();
            transfer.setSender(customer);

            model.addAttribute("transfer",transfer);
            List<Customer> recipients = customerService.findAllByIdNotAndDeletedIsFalse(customerId);
            model.addAttribute("recipients",recipients);
        }
        return "/customer/transfer";

    }

    @PostMapping("/transfer/{customerId}")
    public String toTransfer(@PathVariable Long customerId, Model model, @ModelAttribute Transfer transfer) {
        Optional<Customer> customerOptional = customerService.findById(customerId);
        List<Customer> recipients = customerService.findAllByIdNotAndDeletedIsFalse(customerId);

         model.addAttribute("recipients", recipients);
        model.addAttribute("transfer", transfer);

        if (!customerOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("messages", "Id không tồn tại");

            return "/customer/transfer";
        }

        Long recipientId = transfer.getRecipient().getId();
        Optional<Customer> recipientOptional = customerService.findById(recipientId);

        if (!recipientOptional.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("messages", "Id Recipient không tồn tại");

            return "/customer/transfer";
        }

        if (customerId.equals(recipientId)) {
            model.addAttribute("error", true);
            model.addAttribute("messages", "Người nhận đã trùng với người gửi");

            return "/customer/transfer";
        }

        BigDecimal senderCurrentBalance = customerOptional.get().getBalance();

        String transferAmountStr = String.valueOf(transfer.getTransferAmount());
        BigDecimal transferAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));
        long fees = 10L;
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        if (senderCurrentBalance.compareTo(transactionAmount) < 0) {
            model.addAttribute("error", true);
            model.addAttribute("messages", "không đủ tiền chuyển");

            return "/customer/transfer";
        }
        transfer.setSender(customerOptional.get());
        transfer.setFees(fees);
        transfer.setFeesAmount(feesAmount);
        transfer.setTransactionAmount(transactionAmount);

        customerService.transfer(transfer);

        transfer.setTransferAmount(BigDecimal.ZERO);
        transfer.setTransactionAmount(BigDecimal.ZERO);

        model.addAttribute("transfer", transfer);

        model.addAttribute("success", true);
        model.addAttribute("messages", "chuyển tiền thành công");

        return "/customer/transfer";
    }

    @GetMapping("/transferInfo")
    public String showTransferInfo(Model model){
        List<Transfer> transfers = transferService.findAll();

        model.addAttribute("transfers", transfers);
        BigDecimal totalFeesAmount = transferService.getAllFeesAmount();

        model.addAttribute("transfers", transfers);
        model.addAttribute("totalFeesAmount", totalFeesAmount);

        return "/customer/transferInfo";
    }

    @GetMapping("/depositInfo")
    public String showDepositInfo(Model model){
        List<Deposit> deposits = depositService.findAll();
        model.addAttribute("deposits",deposits);
        return "/customer/depositInfo";
    }
    @GetMapping("/withdrawInfo")
    public String showWithdrawInfo(Model model){
        List<Withdraw> withdraws = withdrawService.findAll();
        model.addAttribute("withdraws",withdraws);
        return "/customer/withdrawInfo";
    }

}
