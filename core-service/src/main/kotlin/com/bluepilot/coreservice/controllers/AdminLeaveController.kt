package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.LeaveService
import com.bluepilot.coreservice.services.UserService
import com.bluepilot.models.responses.LeaveDetailsResponse
import com.bluepilot.models.responses.PageResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/leave")
@PreAuthorize("hasAnyRole('HR','ADMIN')")
class AdminLeaveController @Autowired constructor(leaveService: LeaveService, userService: UserService) :
    AbstractLeaveController(leaveService, userService) {

    @GetMapping("/summary")
    @PreAuthorize("hasPermission('hasAccess','user.leave.view')")
    fun getLeaveSummaryOfAllEmployee(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10
        ): ResponseEntity<PageResponse<LeaveDetailsResponse>> {
        return ResponseEntity.ok().body(leaveService.getLeaveSummaryOfAllEmployee(pageNumber, pageSize))
    }
}