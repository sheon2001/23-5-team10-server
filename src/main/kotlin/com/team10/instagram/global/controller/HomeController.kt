package com.team10.instagram.global.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun home(): String {
        // root(/) -> move to swagger page
        return "redirect:/swagger-ui/index.html"
    }
}
