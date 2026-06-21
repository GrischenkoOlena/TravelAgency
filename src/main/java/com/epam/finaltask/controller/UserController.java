package com.epam.finaltask.controller;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    VoucherService voucherService;
    UserService userService;
    OrderService orderService;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model){
        model.addAttribute("vouchers", voucherService.findAll());
        return "user/dashboard";
    }

    @PostMapping("/{id}/order")
    public String orderTour(@PathVariable("id") String voucherId,
                            @AuthenticationPrincipal UserDetails user, RedirectAttributes redirectAttributes) {
        try {
            String userId = userService.getUserByUsername(user.getUsername()).getId();

            voucherService.order(voucherId, userId);
            redirectAttributes.addFlashAttribute("message", "The tour has been successfully booked!");
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/orders")
    public String viewOrders(@AuthenticationPrincipal UserDetails user, Model model){
        UserDTO userDTO = userService.getUserByUsername(user.getUsername());
        List<OrderDTO> orders = orderService.findOrdersByUserId(userDTO.getId());
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    @PostMapping("/{id}/pay")
    public String payOrder(@PathVariable("id") String orderId, @AuthenticationPrincipal UserDetails user){
        UserDTO userDTO = userService.getUserByUsername(user.getUsername());
        userService.payOrder(orderId, userDTO);
        return "redirect:/orders";
    }


    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails user, Model model){
        String username = user.getUsername();
        UserDTO userDTO = userService.getUserByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("userDTO", userDTO);
        return "user/profileForm";
    }


    @PostMapping("/editProfile")
    public String editProfile(@AuthenticationPrincipal UserDetails user, @ModelAttribute("userDTO") UserProfileDTO formTarget){
        userService.updateProfile(user.getUsername(), formTarget);
        return "redirect:/orders";
    }


}
