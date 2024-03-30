package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.requests.BasicDetailsRequest
import com.bluepilot.coreservice.models.responses.OnboardingContextResponse
import com.bluepilot.coreservice.services.OnboardingContextService
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/employee/user-context")
@Validated
class OnboardingContextController @Autowired constructor(
        onboardingContextService: OnboardingContextService
): AbstractOnBoardingContextController(onboardingContextService) {

    @GetMapping("/validate-code")
    fun validateInvite(@RequestParam inviteCode: String, @RequestParam email: String): ResponseEntity<Response> {
        onboardingContextService.validateInviteCodeAndGetContext(inviteCode, email)
        return ResponseEntity.accepted().body(Response("Valid invite code"))
    }

    @PostMapping("/update")
    fun submitBasicDetails(@Valid @RequestBody basicDetailsRequest: BasicDetailsRequest) : ResponseEntity<OnboardingContextResponse> {
        return ResponseEntity.ok().body(onboardingContextService.saveBasicDetails(basicDetailsRequest))
    }
}
