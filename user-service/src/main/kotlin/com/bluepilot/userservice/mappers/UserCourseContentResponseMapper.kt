package com.bluepilot.userservice.mappers

import com.bluepilot.entities.UserContent
import com.bluepilot.entities.UserCourse
import com.bluepilot.userservice.models.responses.ContentResponse
import com.bluepilot.userservice.models.responses.CourseResponse
import com.bluepilot.userservice.models.responses.UserContentResponse
import com.bluepilot.userservice.models.responses.UserCourseResponse
import com.bluepilot.userservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserCourseContentResponseMapper @Autowired constructor(val userService: UserService) {
    fun mapToUserCourseResponse(userCourses: List<UserCourse>): List<UserCourseResponse> = userCourses.map {
        UserCourseResponse(
            userCourseId = it.id,
            course = it.course.run {
                CourseResponse(
                    courseId = this.id,
                    description = this.description,
                    name = this.name,
                    createdBy = userService
                        .getUserById(this.createdBy).run { "${this.firstName} ${this.lastName}" },
                    hours = it.course.hours
                )
            },
            trainingDetailsId = it.trainingDetails.id,
            userContent = mapToUserContentResponse(it.userContent),
            hoursSpent = it.hoursSpent,
            startedAt = it.startedAt,
            completedAt = it.completedAt,
            status = it.status
        )
    }

    fun mapToUserContentResponse(userContents: List<UserContent>): List<UserContentResponse> = userContents.map {
        UserContentResponse(
            userContentId = it.id,
            content = it.content.run {
                ContentResponse(
                    contentId = this.id,
                    name = this.name,
                    description = this.description,
                    hours = this.hours,
                    pointsAllotted = this.pointsAllotted,
                    type = this.type,
                    files = this.files,
                    links = this.links,
                    createdBy = userService
                        .getUserById(this.createdBy).run { "${this.firstName} ${this.lastName}" }
                )
            },
            contentStatus = it.status,
            startedAt = it.startedAt,
            completedAt = it.completedAt,
            pointsAwarded = it.pointsAwarded
        )
    }
}