package com.bluepilot.coreservice.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class PingController {

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.accepted().body("pong");
    }
}