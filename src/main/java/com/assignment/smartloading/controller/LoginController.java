package com.assignment.smartloading.controller;

import com.assignment.smartloading.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/login"})
    public String showLoginPage(@RequestParam(value="error", required=false) String error,
                                Model model,
                                HttpSession session) {

        // If already logged in → go to dashboard
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password!");
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session) {

        String userId = userService.validateUserAndReturnId(username, password);

        if (userId != null) {

            // GUARANTEE: Session always has userId
            session.setAttribute("userId", userId);

            return "redirect:/dashboard";
        }

        return "redirect:/login?error=true";
    }
}
