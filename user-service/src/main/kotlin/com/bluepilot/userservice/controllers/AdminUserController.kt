package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.models.requests.UpdateESIAndPFDetailsByHRRequest
import com.bluepilot.userservice.models.responses.UserESIAndPFDetailsResponse
import com.bluepilot.userservice.services.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/admin/user")
@Validated
class AdminUserController @Autowired constructor(userService: UserService) : AbstractUserController(userService) {

    @PostMapping("/esi-pf-details/update")
    @PreAuthorize("hasAnyRole('HR','ADMIN') AND hasPermission('hasAccess','user.update')")
    fun updateUserESIAndPFDetailsByHR(
        @Valid @RequestBody updateESIAndPFDetailsByHRRequest: UpdateESIAndPFDetailsByHRRequest
    ): ResponseEntity<UserESIAndPFDetailsResponse> {
        return ResponseEntity.ok().body(userService.updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest))
    }

    @GetMapping("/esi-pf-details/{userId}")
    @PreAuthorize("hasAnyRole('HR','ADMIN') AND hasPermission('hasAccess','user.view')")
    fun getUserESIAndPFDetails(
        @PathVariable userId: Long
    ): ResponseEntity<UserESIAndPFDetailsResponse> {
        return ResponseEntity.ok().body(userService.getESIAndPFDetailsByUserId(userId))
    }
}