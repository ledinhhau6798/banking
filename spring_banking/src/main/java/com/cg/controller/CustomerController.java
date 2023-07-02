package com.cg.controller;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Withdraw;
import com.cg.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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


    @GetMapping
    public String showListCustomer(Model model) {
        List<Customer> customers = customerService.findAll();
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
    public String doCreate(Customer customer, Model model) {
        customer.setBalance(BigDecimal.ZERO);
        customerService.save(customer);

        model.addAttribute("customer", new Customer());

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
    public String toUpdate(@PathVariable Long id, Model model, @ModelAttribute Customer customer) {

        customer.setId(id);
        customerService.save(customer);
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {

        try {
            Long customerId = Long.parseLong(id);
            customerService.deleteById(customerId);

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Xóa thành công");

            return "redirect:/customers";
        } catch (Exception e) {
            return "/errors/404";
        }
    }

    @GetMapping("/deposit/{id}")
    public String showDeposit(@PathVariable String id, Model model) {
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

    @PostMapping("/deposit/{id}")
    public String toDeposit(@PathVariable String id,Model model,@RequestParam("deposit") String deposit){
        try{
            Long customerId = Long.parseLong(id);
             Long depositAmount = Long.parseLong(deposit);
            Optional<Customer> customerOptional = customerService.findById(customerId);

             if (customerOptional.isEmpty()) {
                return "redirect:/errors/404";
            }

             Customer customer = customerOptional.get();

             BigDecimal balance = customer.getBalance().add(BigDecimal.valueOf(depositAmount));

             customer.setBalance(balance);
             customerService.save(customer);
             Deposit deposit1 = new Deposit();
             deposit1.setCustomer(customer);
             model.addAttribute("deposit",deposit1);
            return "/customer/deposit";


        }catch (Exception e){
            return "/errors/404";
        }
    }

    @GetMapping("/withdraw/{id}")
    public String showWithdraw(@PathVariable String id, Model model) {
        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customerOptional = customerService.findById(customerId);

            if (customerOptional.isEmpty()) {
                return "redirect:/errors/404";
            }

            Customer customer = customerOptional.get();

            Withdraw withdraw = new Withdraw();
            withdraw.setCustomer(customer);

            model.addAttribute("withdraw", withdraw);

            return "/customer/withdraw";
        } catch (Exception e) {
            return "/errors/404";
        }

    }

    @PostMapping("/withdraw/{id}")
    public String toWithdraw(@PathVariable String id,Model model,@RequestParam("withdraw") String withdraw){
        try{
            Long customerId = Long.parseLong(id);
             Long withdrawAmount = Long.parseLong(withdraw);
            Optional<Customer> customerOptional = customerService.findById(customerId);

             if (customerOptional.isEmpty()) {
                return "redirect:/errors/404";
            }

             Customer customer = customerOptional.get();

             BigDecimal balance = customer.getBalance().subtract(BigDecimal.valueOf(withdrawAmount));

             customer.setBalance(balance);
             customerService.save(customer);
             Withdraw withdraw1 = new Withdraw();
             withdraw1.setCustomer(customer);
             model.addAttribute("withdraw",withdraw1);
            return "/customer/withdraw";


        }catch (Exception e){
            return "/errors/404";
        }
    }



}
