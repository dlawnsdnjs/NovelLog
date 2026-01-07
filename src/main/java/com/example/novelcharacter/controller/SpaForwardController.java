package com.example.novelcharacter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaForwardController {

    @RequestMapping({
            "/page",
            "/page/**"
    })
    public String forwardPage() {
        return "forward:/index.html";
    }
}