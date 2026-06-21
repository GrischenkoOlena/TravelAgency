package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class CommonController {
    private UserService userService;
    private VoucherService voucherService;

    @GetMapping("/")
    public String handleIndex(Model model) {
        model.addAttribute("vouchers", voucherService.findAll());
        return "index";
    }

    @GetMapping("/cabinet")
    public String viewCabinet(@AuthenticationPrincipal UserDetails user){
        String redirectUrl = "redirect:/";
        if(user == null){
            return redirectUrl;
        }

        UserDTO currentUser = userService.getUserByUsername(user.getUsername());
        Role userRole = Role.valueOf(currentUser.getRole());
        switch (userRole){
            case USER -> redirectUrl = "redirect:/dashboard";
            case ADMIN -> redirectUrl = "redirect:/users";
            case MANAGER -> redirectUrl = "redirect:/tours";
        }
        return redirectUrl;
    }
}
