package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.responses.BasicDetailsResponse
import com.bluepilot.coreservice.services.OnboardingContextService
import com.bluepilot.entities.OnboardingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractOnBoardingContextController @Autowired constructor(
    val onboardingContextService: OnboardingContextService
) {

    @GetMapping("/view")
    fun getOnboardingContext(
        @RequestParam inviteCode: String,
        @RequestParam email: String
    ): ResponseEntity<BasicDetailsResponse> {
        return ResponseEntity.ok().body(onboardingContextService.validateInviteCodeAndGetContext(inviteCode, email))
    }
}