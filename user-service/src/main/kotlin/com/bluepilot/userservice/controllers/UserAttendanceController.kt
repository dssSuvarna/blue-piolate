package com.bluepilot.userservice.controllers

import com.bluepilot.enums.AttendanceEvent
import com.bluepilot.models.responses.Response
import com.bluepilot.userservice.services.AttendanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/attendance")
class UserAttendanceController @Autowired constructor(
    attendanceService: AttendanceService
) : AbstractAttendanceController(attendanceService) {

    @PostMapping
    @PreAuthorize("hasPermission('hasAccess','user.attendance.update')")
    fun markAttendance(
        @RequestHeader(name = "Authorization") token: String, @RequestBody event: AttendanceEvent
    ): ResponseEntity<Response> = ResponseEntity.ok().body(attendanceService.attendanceEventHandler(token, event))
}