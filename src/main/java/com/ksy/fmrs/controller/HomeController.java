package com.ksy.fmrs.controller;

import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final PlayerService playerService;

    @GetMapping("/api/home")
    public String home() {
        return "home";
    }

}
