package com.tansiyon.tansiyon_takip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaglikController {

    @GetMapping("/saglik")
    public String saglik() {
        return "OK";
    }

    @GetMapping("/")
    public String anaSayfa() {
        return "Tansiyon Takip API çalışıyor. /saglik -> OK";
    }
}
