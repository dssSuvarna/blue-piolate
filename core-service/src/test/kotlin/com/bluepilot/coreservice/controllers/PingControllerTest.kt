package com.bluepilot.coreservice.controllers

import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@SpringBootTest(classes = [PingController::class])
class PingControllerTest {
    @Mock
    lateinit var restTemplate: RestTemplate

    @Test
    fun shouldReturnPong() {
        `when`(restTemplate.getForEntity("http://localhost:8080/core-service/ping", String::class.java))
            .thenReturn(ResponseEntity("pong", HttpStatus.OK))
    }
}