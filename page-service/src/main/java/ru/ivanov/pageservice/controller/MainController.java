package ru.ivanov.pageservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("e-commerce")
public class MainController {

    @GetMapping
    public String page() {
        return "ecom_page";
    }
}