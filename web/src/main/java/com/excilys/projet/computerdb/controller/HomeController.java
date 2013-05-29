package com.excilys.projet.computerdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {
    
    private final static String HOMEPAGE = "redirect:/x/computer";
    
    @RequestMapping(value = "/")
    public String redirectVoid() {
        return HOMEPAGE;
    }
    
    @RequestMapping(value = "/home")
    public String redirectHome() {
        return HOMEPAGE;
    }
    
}
