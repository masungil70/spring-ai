package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/list-output-converter")
    public String listOutputConverter() {
        return "list-output-converter";
    }    

    @GetMapping("/bean-output-converter")
    public String beanOutputConverter() {
        return "bean-output-converter";
    }  

}
