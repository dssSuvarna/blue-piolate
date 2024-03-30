package com.bluepilot.userservice.controllers

import com.bluepilot.models.requests.LeaveRequest
import com.bluepilot.models.requests.LeavesFilter
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.models.responses.Response
import com.bluepilot.userservice.services.LeaveService
import com.bluepilot.userservice.services.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/leave")
@Validated
class LeaveController @Autowired constructor(
    val leaveService: LeaveService,
    val userService: UserService
) {

    @PostMapping("/apply")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess','user.leave.apply')")
    fun applyLeave(
        @RequestHeader(name = "Authorization") token: String,
        @Valid @RequestBody leaveRequest: LeaveRequest
    ): ResponseEntity<Response> {
        val user = userService.getUserFromToken(token)
        leaveService.applyLeave(leaveRequest, user)
        return ResponseEntity.ok().body(Response("Leave applied"))
    }

    @PostMapping("/summary")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess','user.leave.view')")
    fun viewLeaveSummary(
        @RequestHeader(name = "Authorization") token: String,
        @RequestBody leavesFilter: LeavesFilter
    ): ResponseEntity<LeaveDetailsResponse> {
        val user = userService.getUserFromToken(token)
        return ResponseEntity.ok().body(leaveService.getLeaveSummary(user, leavesFilter))
    }
}