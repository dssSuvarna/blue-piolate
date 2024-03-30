package com.bluepilot.userservice.controllers

import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import com.bluepilot.userservice.models.requests.CreateCourseRequest
import com.bluepilot.userservice.models.responses.CourseResponse
import com.bluepilot.userservice.models.responses.UserCourseResponse
import com.bluepilot.userservice.services.CourseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractCourseController @Autowired constructor(
    val courseService: CourseService
) {
    @PostMapping
    @PreAuthorize("hasPermission('hasAccess','user.course.create')")
    fun addCourse(
        @RequestBody createCourseRequest: CreateCourseRequest,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<CourseResponse> {
        return ResponseEntity.ok().body(courseService.saveCourse(createCourseRequest, token))
    }

    @GetMapping
    @PreAuthorize("hasPermission('hasAccess','user.course.view')")
    fun getAllCourse(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
    ): ResponseEntity<PageResponse<CourseResponse>> {
        return ResponseEntity.ok().body(courseService.getAllCoursesPaged(pageNumber,pageSize))
    }

    @PostMapping("/assign")
    @PreAuthorize("hasPermission('hasAccess','user.course.assign')")
    fun assignCourse(
        @RequestParam courseId: Long,
        @RequestParam trainingDetailsId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        courseService.assignCourseToUser(courseId, trainingDetailsId, token)
        return ResponseEntity.ok().body(Response("Course assigned"))
    }

    @GetMapping("/user-courses/{trainingDetailsId}")
    @PreAuthorize("hasPermission('hasAccess','user.course.view')")
    fun getCoursesAssignedToUser(
        @PathVariable trainingDetailsId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<List<UserCourseResponse>> =
        ResponseEntity.ok().body(courseService.getUserCoursesOfUser(trainingDetailsId, token))

    @GetMapping("/{courseId}")
    @PreAuthorize("hasPermission('hasAccess','user.course.view')")
    fun getCourseById(
        @PathVariable courseId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<CourseResponse> =
        ResponseEntity.ok().body(courseService.getCourseById(courseId))
}