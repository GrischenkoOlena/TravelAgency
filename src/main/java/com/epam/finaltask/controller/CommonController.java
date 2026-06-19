package com.epam.finaltask.controller;

import com.epam.finaltask.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class CommonController {
    VoucherService voucherService;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model){
        model.addAttribute("vouchers", voucherService.findAll());
        return "user/dashboard";
    }
}
