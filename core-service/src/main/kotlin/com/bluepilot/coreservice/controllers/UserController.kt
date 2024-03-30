package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/user")
@Validated
class UserController @Autowired constructor(userService: UserService) : AbstractUserController(userService)