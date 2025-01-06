package aayush.librarymanagementsystem.controller;

import aayush.librarymanagementsystem.dto.Account;
import aayush.librarymanagementsystem.service.NotificationService;
import aayush.librarymanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_LIBRARIAN"})
    public String getNotifications(Model model, Principal principal) {
        // find the user by email
        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
            model.addAttribute("notifications", notificationService.getNotifications(principal.getName()));
        } else {
            model.addAttribute("account", null);
            model.addAttribute("notifications", List.of());
        }

        return "notifications";
    }
}
