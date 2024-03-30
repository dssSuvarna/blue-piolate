package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.services.AttendanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractAttendanceController @Autowired constructor(val attendanceService: AttendanceService)