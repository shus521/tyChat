package com.tyss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class tyChatController {

    @GetMapping("/tyChat")
    public String tyChat() {
        return "test";
    }
}
