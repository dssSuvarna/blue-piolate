package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.HolidayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/holidays")
class UserHolidayController @Autowired constructor(holidayService: HolidayService) :
    AbstractHolidayController(holidayService)