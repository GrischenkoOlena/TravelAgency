package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.UnableChangeStatusException;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.epam.finaltask.config.AppConstants.*;

@Slf4j
@Controller
@PreAuthorize("hasRole('MANAGER')")
@AllArgsConstructor
public class ManagerController {
    VoucherService voucherService;
    OrderService orderService;

    @GetMapping("/tours")
    public String viewDashboard(@RequestParam(name = "page", defaultValue = PAGE_NUMBER) Integer pageNumber,
                                @RequestParam(name = "size", defaultValue = SIZE_PAGE) Integer size,
                                @RequestParam(name = "sort", defaultValue = DEFAULT_TOUR_SORT) String orderFields, Model model){
        Pageable newPage = PageRequest.of(pageNumber, size, Sort.by(orderFields).descending());
        model.addAttribute("voucherPage", voucherService.findAll(newPage));
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
        if (checkUncorrectedDate(voucherDTO)) {
            model.addAttribute("errorMessage", "date arrival must be earlier than date eviction");
            return "manager/tourForm";
        }
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
                             @ModelAttribute("voucherDTO") @Valid VoucherDTO voucherDTO,
                             BindingResult result, Model model){
        if(result.hasErrors()) {
            model.addAttribute("voucherDTO", voucherDTO);
            String errorMessage = result.getFieldError() != null
                    ? result.getFieldError().getDefaultMessage()
                    : "Invalid input data";
            model.addAttribute("errorMessage", errorMessage);
            return "manager/updateTourForm";
        } else {
            voucherService.update(voucherId, voucherDTO);
            return "redirect:/tours";
        }
    }

    @PostMapping("/{id}/deleteTour")
    public String deleteTour(@PathVariable("id") String voucherId, RedirectAttributes redirectAttributes){
        try {
            voucherService.delete(voucherId);
        } catch (Exception e) {
            log.warn("delete tour was failed");
            redirectAttributes.addFlashAttribute("message", "tour with orders cannot be deleted");
        }
        return "redirect:/tours";
    }

    @PostMapping("/{id}/canceled")
    public String canceledOrder(@PathVariable("id") String orderId, RedirectAttributes redirectAttributes){
        try {
            orderService.canceledOrder(orderId);
        } catch (UnableChangeStatusException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
        }
        return "redirect:/allOrders";
    }

    private boolean checkUncorrectedDate(VoucherDTO voucherDTO){
        return voucherDTO.getArrivalDate().isAfter(voucherDTO.getEvictionDate());
    }

}
