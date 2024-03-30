package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.LeaveService
import com.bluepilot.coreservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/leave")
@PreAuthorize("hasRole('EMPLOYEE')")
class UserLeaveController @Autowired constructor(leaveService: LeaveService, userService: UserService) :
    AbstractLeaveController(leaveService, userService)