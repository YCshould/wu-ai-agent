package com.wu.wuaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/love")
public class LoveController {

    @GetMapping("/ok")
    public String loveCheck() {
        return "ok";
    }
}

