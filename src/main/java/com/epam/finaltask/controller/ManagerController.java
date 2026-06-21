package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@AllArgsConstructor
public class ManagerController {
    VoucherService voucherService;
    OrderService orderService;

    @GetMapping("/tours")
    public String viewDashboard(Model model){
        model.addAttribute("vouchers", voucherService.findAll());
        return "manager/tours";
    }

    @GetMapping("/allOrders")
    public String viewAllOrders(Model model){
        model.addAttribute("orders", orderService.findAll());
        return "manager/allOrders";
    }

    @GetMapping("/addTour")
    public String showTourForm(Model model){
        if (!model.containsAttribute("voucherDTO")) {
            model.addAttribute("voucherDTO", new VoucherDTO());
        }
        return "manager/tourForm";
    }

    @PostMapping("/addTour")
    public String addNewTour(@ModelAttribute("voucherDTO") @Valid VoucherDTO voucherDTO, Model model){
        try {
            voucherService.create(voucherDTO);
            model.addAttribute(voucherDTO);
            log.info("tour has been added successfully");
            return "redirect:/tours";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid values");
            log.info("adding tour failed {}", e.getMessage());
            return "manager/tourForm";
        }
    }

    @GetMapping("/{id}/updateTour")
    public String showUpdateTourForm(@PathVariable("id") String voucherId, Model model){
        VoucherDTO voucherDTO = voucherService.findById(voucherId);
        model.addAttribute("voucherDTO", voucherDTO);
        model.addAttribute("voucherId", voucherId);
        return "manager/updateTourForm";
    }

    @PostMapping("/{id}/updateTour")
    public String updateTour(@PathVariable("id") String voucherId,
                             @ModelAttribute("voucherDTO") @Valid VoucherDTO voucherDTO){
        voucherService.update(voucherId, voucherDTO);
        return "redirect:/tours";
    }

    @PostMapping("/{id}/deleteTour")
    public String deleteTour(@PathVariable("id") String voucherId){
        voucherService.delete(voucherId);
        return "redirect:/tours";
    }

    @PostMapping("/{id}/canceled")
    public String canceledOrder(@PathVariable("id") String orderId){
        orderService.canceledOrder(orderId);
        return "redirect:/allOrders";
    }

}
