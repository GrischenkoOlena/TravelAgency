package com.epam.finaltask.controller;

import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.auth.LoginRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authService;
    UserService userService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "auth/sign-in";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") @Valid LoginRequest loginRequest,
                              Model model, HttpServletResponse response) {
        try {
            String token = authService.authenticateAndGenerateToken(loginRequest);

            // Package token into a secure, HTTP-only Cookie
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);      // Prevents JavaScript access (XSS protection)
            jwtCookie.setSecure(false);       // Set to true in production (HTTPS)
            jwtCookie.setPath("/");           // Available across the entire site
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 day expiration

            response.addCookie(jwtCookie);

            String username = loginRequest.getUsername();
            Role userRole = Role.valueOf(userService.getUserByUsername(username).getRole());

            log.info("handle login for user {} with role {}", username, userRole);

            String redirectUrl = "redirect:/";
            switch (userRole){
                case USER -> redirectUrl = "redirect:/dashboard";
                case ADMIN -> redirectUrl = "redirect:/users";
                case MANAGER -> redirectUrl = "redirect:/tours";
            }
            return redirectUrl;

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid username or password");
            log.info("login with error (Invalid username or password)");

            return "auth/sign-in";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", new UserDTO());
        }
        return "auth/registrationForm";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("errorMessage", result.getFieldError().getDefaultMessage());
            model.addAttribute("userDTO", userDTO);
            return "auth/registrationForm";
        }
        try {
            userService.register(userDTO);
            model.addAttribute(userDTO);
            log.info("handle registration user {}", userDTO.getUsername());
            return "redirect:/auth/login";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid registration data");
            log.info("registration was failed (Invalid registration data)");

            return "auth/registrationForm";
        }
    }

}
