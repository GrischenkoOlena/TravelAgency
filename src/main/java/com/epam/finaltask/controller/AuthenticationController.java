package com.epam.finaltask.controller;

import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.auth.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/sign-in")
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
            System.out.println("DEBUG -> LoginRequest is " + loginRequest);
            String token = authService.authenticateAndGenerateToken(loginRequest);

            // Package token into a secure, HTTP-only Cookie
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);      // Prevents JavaScript access (XSS protection)
            jwtCookie.setSecure(false);       // Set to true in production (HTTPS)
            jwtCookie.setPath("/");           // Available across the entire site
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 day expiration

            response.addCookie(jwtCookie);

            log.info("handle login with {}", token);
            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid username or password");
            log.info("login with error (Invalid username or password)");

            return "auth/sing-in";
        }
    }

}
