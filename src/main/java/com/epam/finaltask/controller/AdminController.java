package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController {
    UserService userService;

    @GetMapping("/users")
    public String viewDashboard(Model model){
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/addManager")
    public String viewAddUserForm(Model model){
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", new UserProfileDTO());
        }
        return "admin/newUserForm";
    }

    @PostMapping("/addManager")
    public String addNewManager(@ModelAttribute("userDTO") @Valid UserDTO userDTO, Model model){
        try {
            userService.createManager(userDTO);
            model.addAttribute(userDTO);
            log.info("manager has been added successfully");
            return "redirect:/users";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid values");
            log.info("adding manager failed {}", e.getMessage());
            return "admin/newUserForm";
        }
    }

    @PostMapping("/{id}/blockedUser")
    public String blockedUser(@PathVariable("id") String userId){
        userService.changeAccountStatus(userId);
        return "redirect:/users";
    }

    @GetMapping("/{id}/updateUser")
    public String showUpdateUserForm(@PathVariable("id") String username, Model model){
        UserDTO userDTO = userService.getUserByUsername(username);
        model.addAttribute("userDTO", userDTO);
        model.addAttribute("username", username);
        return "admin/updateUserForm";
    }

    @PostMapping("/{id}/updateUser")
    public String updateUser(@PathVariable("id") String username,
                             @ModelAttribute("userDTO") @Valid UserDTO userDTO,
                             BindingResult result, Model model){
        if(result.getFieldErrorCount() == 1 && result.getFieldError().getField().equals("password")){
            userService.updateUser(username, userDTO);
            return "redirect:/users";
        } else {
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("username", username);
            String errorMessage = result.getFieldErrors().stream()
                    .filter(error -> !error.getField().equals("password"))
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(java.util.stream.Collectors.joining(", "));
            model.addAttribute("errorMessage", errorMessage);
            return "admin/updateUserForm";
        }
    }

    @PostMapping("/{id}/deleteUser")
    public String deleteUser(@PathVariable("id") String userId){
        userService.deleteUser(userId);
        return "redirect:/users";
    }

}
