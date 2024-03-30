package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.models.responses.EmployeeSummary
import com.bluepilot.userservice.models.responses.UserAcademicDetailsResponse
import com.bluepilot.userservice.models.responses.UserBankDetailsResponse
import com.bluepilot.userservice.models.responses.UserDetailedResponse
import com.bluepilot.userservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AbstractUserController @Autowired constructor(
    val userService: UserService
){

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('hasAccess','user.view')")
    fun getUser(@PathVariable("id") userId: Long): ResponseEntity<UserDetailedResponse> {
        return ResponseEntity.ok().body(userService.getUserDetailedResponseByUserId(userId))
    }

    @GetMapping("/bank-details/{userId}")
    @PreAuthorize("hasPermission('hasAccess','user.view')")
    fun getUserBankDetails(
        @PathVariable userId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<UserBankDetailsResponse> {
        return ResponseEntity.ok().body(userService.getBankDetails(userId, token.substring(7)))
    }

    @GetMapping("/academic-details/{userId}")
    @PreAuthorize("hasPermission('hasAccess','user.view')")
    fun getUserAcademicDetails(
        @PathVariable userId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<UserAcademicDetailsResponse> {
        return ResponseEntity.ok().body(userService.getAcademicDetails(userId, token.substring(7)))
    }

    @GetMapping("/summary")
    @PreAuthorize("hasPermission('hasAccess','user.view')")
    fun getAllUsersSummary(): ResponseEntity<List<EmployeeSummary>> =
        ResponseEntity.ok().body(userService.getAllUsersSummary())
}