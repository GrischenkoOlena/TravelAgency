package com.epam.finaltask.controller;

import com.epam.finaltask.dto.OrderDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.NotEnoughMoneyException;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.OrderService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.epam.finaltask.config.AppConstants.*;

@Controller
@AllArgsConstructor
public class UserController {
    VoucherService voucherService;
    UserService userService;
    OrderService orderService;

    @GetMapping("/dashboard")
    public String viewDashboard(@RequestParam(name = "page", defaultValue = PAGE_NUMBER) Integer pageNumber,
                                @RequestParam(name = "size", defaultValue = SIZE_PAGE) Integer size,
                                @RequestParam(name = "sort", defaultValue = DEFAULT_TOUR_SORT) String sort,
                                @RequestParam(defaultValue = "desc") String dir,
                                @RequestParam(required = false) TourType tourType,
                                @RequestParam(required = false) HotelType hotelType,
                                @RequestParam(required = false) TransferType transferType,
                                Model model){

        Pageable newPage = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.fromString(dir), sort));
        Page<VoucherDTO> result;
        if (tourType == null && hotelType == null && transferType == null){
            result = voucherService.findAll(newPage);
        } else {
            result = voucherService.search(tourType, transferType, hotelType, newPage);
        }

        model.addAttribute("voucherPage", result);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("tourType", tourType);
        model.addAttribute("hotelType", hotelType);
        model.addAttribute("transferType", transferType);

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
    public String payOrder(@PathVariable("id") String orderId, @AuthenticationPrincipal UserDetails user, RedirectAttributes redirectAttributes){
        try {
            UserDTO userDTO = userService.getUserByUsername(user.getUsername());
            userService.payOrder(orderId, userDTO);
        } catch (NotEnoughMoneyException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
        }
        return "redirect:/orders";
    }


    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails user, Model model){
        String username = user.getUsername();
        UserDTO userDTO = userService.getUserByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("errorMessage", "");
        return "user/profileForm";
    }


    @PostMapping("/editProfile")
    public String editProfile(@AuthenticationPrincipal UserDetails user,
                              @Valid @ModelAttribute("userDTO") UserProfileDTO formTarget,
                              BindingResult result, Model model){
        if(result.hasErrors()) {
            model.addAttribute("userDTO", formTarget);
            String errorMessage = result.getFieldError() != null
                    ? result.getFieldError().getDefaultMessage()
                    : "Invalid input data";
            model.addAttribute("errorMessage", errorMessage);
            return "user/profileForm";
        } else {
            userService.updateProfile(user.getUsername(), formTarget);
            return "redirect:/dashboard";
        }
    }


}
