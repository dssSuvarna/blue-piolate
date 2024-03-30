package com.bluepilot.userservice.controllers

import com.bluepilot.models.responses.Response
import com.bluepilot.userservice.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/course")
class UserCourseController @Autowired constructor(
    courseService: CourseService
): AbstractCourseController(courseService) {

    @PostMapping("/start/{userCourseId}")
    @PreAuthorize("hasRole('EMPLOYEE') and hasPermission('hasAccess','user.course.start')")
    fun startCourse(
        @PathVariable userCourseId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        courseService.startCourse(userCourseId,token)
        return ResponseEntity.ok().body(Response("Course started"))
    }
}