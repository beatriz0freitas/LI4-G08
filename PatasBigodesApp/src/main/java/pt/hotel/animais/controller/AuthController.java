package pt.hotel.animais.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String root(Model model) {
        model.addAttribute("activePage", "home");
        return "home/index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
