package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("xinxin")
    public String index() {
        return "redirect:/xinxin";
    }

    public static void main(String[] args) {

    }
    
    
}

