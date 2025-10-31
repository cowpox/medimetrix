package com.mmx.medimetrix.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("msg", "MediMetrix rodando!");
        return "relatorios/home";
    }
}
