package com.bluepilot.userservice.mappers

import com.bluepilot.entities.Contents
import com.bluepilot.entities.Courses
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.UserRepository
import com.bluepilot.userservice.models.responses.ContentResponse
import com.bluepilot.userservice.models.responses.CourseResponse
import com.bluepilot.userservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CourseContentResponseMapper @Autowired constructor(
    val userRepository: UserRepository
) {
    fun mapToCourseResponse(course: Courses): CourseResponse{
        val courseCreator = userRepository.findById(course.createdBy).orElseThrow { NotFoundException(UserNotFound()) }
        return CourseResponse(
            courseId = course.id,
            name = course.name,
            description = course.description,
            contents = mapToContentResponse(course.contents),
            hours = course.hours,
            createdBy = "${courseCreator.firstName} ${courseCreator.lastName}"
        )
    }

    fun mapToContentResponse(contents: List<Contents>): List<ContentResponse>{
        return contents.map {
            val contentCreator = userRepository.findById(it.createdBy).orElseThrow { NotFoundException(UserNotFound()) }
            ContentResponse(
                contentId = it.id,
                name = it.name,
                description = it.description,
                hours = it.hours,
                type = it.type,
                files = it.files,
                links = it.links,
                pointsAllotted = it.pointsAllotted,
                createdBy = "${contentCreator.firstName} ${contentCreator.lastName}"
            )
        }
    }
}