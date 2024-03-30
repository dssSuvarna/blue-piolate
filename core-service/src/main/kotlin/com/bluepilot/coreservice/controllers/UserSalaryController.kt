package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.SalaryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/salary")
@Validated
class UserSalaryController @Autowired constructor(
    salaryService: SalaryService
) : AbstractSalaryController(salaryService)