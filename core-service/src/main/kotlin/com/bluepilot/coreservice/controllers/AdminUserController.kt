package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.requests.CreateBankDetailsRequest
import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.coreservice.models.requests.UnAssignUserSystemResourceRequest
import com.bluepilot.coreservice.models.requests.UpdateUserResourceRequest
import com.bluepilot.coreservice.models.responses.BankDetailsResponse
import com.bluepilot.coreservice.models.responses.UserResourceResponse
import com.bluepilot.coreservice.models.responses.UserResponse
import com.bluepilot.coreservice.models.responses.UserUnAssignSystemResourceResponse
import com.bluepilot.coreservice.services.UserService
import com.bluepilot.enums.UserStatus
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/user")
@Validated
class AdminUserController @Autowired constructor(
    userService: UserService
) : AbstractUserController(userService) {

    @PostMapping("/register")
    @PreAuthorize("hasPermission('hasAccess','user.register')")
    fun registerUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.ok().body(userService.registerUser(request))
    }

    @PostMapping("/bank-details/{userId}")
    @PreAuthorize("hasPermission('hasAccess','user.update.bankdetails')")
    fun addBankDetails(
        @PathVariable userId: Long,
        @Valid @RequestBody createBankDetailsRequest: CreateBankDetailsRequest
    ): ResponseEntity<BankDetailsResponse> {
        return ResponseEntity.ok().body(userService.addBankDetails(userId, createBankDetailsRequest))
    }

    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('HR','ADMIN') AND hasPermission('hasAccess','user.status.change')")
    fun changeUserStatus(
        @RequestParam userId: Long, @RequestParam status: UserStatus
    ): ResponseEntity<Response> {
        userService.changeStatus(status, userId)
        return ResponseEntity.ok().body(Response("User status updated"))
    }

    @PostMapping("/update/resource")
    @PreAuthorize("hasAnyRole('HR','ADMIN') AND hasPermission('hasAccess','user.update.weekoff')")
    fun updateResourcesForUser(@Valid @RequestBody updateUserResourceRequest: UpdateUserResourceRequest)
            : ResponseEntity<UserResourceResponse> {
        return ResponseEntity.ok().body(userService.updateResourceForUser(updateUserResourceRequest))
    }

    @PostMapping("/un-assign/system-resource")
    @PreAuthorize("hasPermission('hasAccess','user.resources.update')")
    fun unAssignSystemResourcesFromUser(@Valid @RequestBody unAssignUserSystemResourceRequest: UnAssignUserSystemResourceRequest)
            : ResponseEntity<UserUnAssignSystemResourceResponse> {
        return ResponseEntity.ok().body(userService.unAssignSystemResourceFromUser(unAssignUserSystemResourceRequest))
    }

    @GetMapping("/resource/{userId}")
    @PreAuthorize("hasPermission('hasAccess','user.resources.view')")
    fun fetchUserResource(@PathVariable userId: Long): ResponseEntity<UserResourceResponse> {
        return ResponseEntity.ok().body(userService.fetchUserResourceByUserId(userId))
    }

    @PutMapping("/saturday-off")
    @PreAuthorize("hasPermission('hasAccess','user.update')")
    fun updateSaturdayOff( @RequestParam saturdayOff: Boolean, @RequestParam userId: Long): Response {
        userService.updateSaturdayOffByUserId(userId, saturdayOff)
        return Response("Week-Off updated")
    }
}