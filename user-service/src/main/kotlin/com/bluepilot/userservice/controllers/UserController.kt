package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.models.requests.UpdateESIAndPFDetailsByUserRequest
import com.bluepilot.userservice.models.requests.UpdateUserDetailsRequest
import com.bluepilot.userservice.models.responses.UserDetailedResponse
import com.bluepilot.userservice.models.responses.UserESIAndPFDetailsResponse
import com.bluepilot.userservice.services.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/user")
@Validated
class UserController @Autowired constructor(userService: UserService): AbstractUserController(userService) {

    @PutMapping("/details/update")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess', 'user.update')")
    fun updateDetails(
        @Valid @RequestBody updateUserDetailsRequest: UpdateUserDetailsRequest,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<UserDetailedResponse> {
        return ResponseEntity.ok().body(userService.updateUserDetails(updateUserDetailsRequest, token.substring(7)))
    }

    @PostMapping("/esi-pf-details/update")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess','user.update')")
    fun addUserESIAndPFDetailsByUser(
        @Valid @RequestBody updateESIAndPFDetailsByUserRequest: UpdateESIAndPFDetailsByUserRequest
    ): ResponseEntity<UserESIAndPFDetailsResponse> {
        return ResponseEntity.ok().body(userService.saveESIAndPFDetailsByUser(updateESIAndPFDetailsByUserRequest))
    }

    @GetMapping("/esi-pf-details")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess','user.view')")
    fun getUserESIAndPFDetails(
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<UserESIAndPFDetailsResponse> {
        return ResponseEntity.ok().body(userService.getESIAndPFDetailsForUser(token))
    }
}