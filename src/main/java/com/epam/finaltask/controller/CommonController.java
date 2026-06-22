package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.epam.finaltask.config.AppConstants.*;

@Controller
@AllArgsConstructor
public class CommonController {
    private UserService userService;
    private VoucherService voucherService;

    @GetMapping("/")
    public String handleIndex(@RequestParam(name = "page", defaultValue = PAGE_NUMBER) Integer pageNumber,
                              @RequestParam(name = "size", defaultValue = SIZE_PAGE) Integer size,
                              @RequestParam(name = "sort", required = false) String orderFields, Model model) {
        Pageable newPage = (orderFields == null) ?
                PageRequest.of(pageNumber, size) : PageRequest.of(pageNumber, size, Sort.by(orderFields));
        model.addAttribute("voucherPage", voucherService.findAll(newPage));
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
