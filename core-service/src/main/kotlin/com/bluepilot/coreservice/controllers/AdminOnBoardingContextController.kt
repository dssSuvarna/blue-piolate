package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.responses.OnboardingContextResponse
import com.bluepilot.coreservice.services.OnboardingContextService
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user-context")
@Validated
class AdminOnBoardingContextController @Autowired constructor(
    onboardingContextService: OnboardingContextService
) : AbstractOnBoardingContextController(onboardingContextService) {

    @PostMapping("/invite")
    @PreAuthorize("hasPermission('hasAccess','user.onboarding.context.invite')")
    fun sendInvite(
        @Valid @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$") @RequestParam email: String,
        @NotNull @RequestParam name: String,
        @RequestParam comment: String?,
    ): ResponseEntity<*>? {
        return ResponseEntity.accepted().body(Response(onboardingContextService.inviteUser(email, name, comment)))
    }

    @PutMapping("/update-status")
    @PreAuthorize("hasPermission('hasAccess','user.onboarding.context.update')")
    fun updateOnboardingContextStatus(
        @RequestParam status: String,
        @RequestParam onboardingContextId: Long
    ): ResponseEntity<Response> {
        onboardingContextService.updateOnboardingContextStatus(onboardingContextId, status)
        return ResponseEntity.ok().body(Response("Details $status"))
    }

    @GetMapping
    @PreAuthorize("hasPermission('hasAccess','user.onboarding.context.view')")
    fun getOnboardingContexts(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10
    ): ResponseEntity<PageResponse<OnboardingContextResponse>> {
        return ResponseEntity.ok().body(onboardingContextService.getAllOnboardingContexts(pageNumber, pageSize))
    }
}