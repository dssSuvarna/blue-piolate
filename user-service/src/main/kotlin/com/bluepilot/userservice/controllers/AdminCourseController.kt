package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/course")
class AdminCourseController @Autowired constructor(
    courseService: CourseService
): AbstractCourseController(courseService)