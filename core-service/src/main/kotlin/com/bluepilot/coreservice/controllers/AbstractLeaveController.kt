package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.responses.LeavesApprovalResponse
import com.bluepilot.coreservice.services.LeaveService
import com.bluepilot.coreservice.services.UserService
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.models.requests.LeavesApprovalFilter
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import com.bluepilot.models.responses.UpcomingLeaveResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractLeaveController @Autowired constructor(
    val leaveService: LeaveService,
    val userService: UserService
) {
    @PutMapping("/approve")
    @PreAuthorize("hasPermission('hasAccess','user.leave.approve')")
    fun approveLeave(
        @RequestParam status: LeaveStatus,
        @RequestParam leaveId: Long,
        @RequestHeader(name = "Authorization") token: String,
    ): ResponseEntity<Response> {
        val user = userService.getUserFromToken(token)
        leaveService.approveOrRejectLeave(leaveId, status, user)
        return ResponseEntity.ok().body(Response("Leave $status"))
    }

    @PostMapping("/approvals")
    @PreAuthorize("hasPermission('hasAccess','user.leave.approve')")
    fun getLeavesToBeApproved(
        @RequestHeader("Authorization") token: String,
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestBody leavesApprovalFilter: LeavesApprovalFilter
    ): ResponseEntity<PageResponse<LeavesApprovalResponse>> {
        val user = userService.getUserFromToken(token)
        return ResponseEntity.ok()
            .body(leaveService.getLeavesApprovalsWithFilter(user, pageNumber, pageSize, leavesApprovalFilter))
    }

    @GetMapping("/upcoming-leaves")
    @PreAuthorize("hasPermission('hasAccess','user.leave.view')")
    fun getUpcomingLeavesOfEmployees(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestParam dateRange: Long = 10L,
    ): ResponseEntity<PageResponse<UpcomingLeaveResponse>> {
        return ResponseEntity.ok()
            .body(leaveService.getUpcomingLeavesOfEmployees(pageNumber, pageSize, dateRange))
    }
}