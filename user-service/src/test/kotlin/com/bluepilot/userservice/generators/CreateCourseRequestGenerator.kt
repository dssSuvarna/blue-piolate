package com.bluepilot.userservice.generators

import com.bluepilot.enums.ContentType
import com.bluepilot.userservice.models.requests.CreateContentRequest
import com.bluepilot.userservice.models.requests.CreateCourseRequest

object CreateCourseRequestGenerator {
    fun generateCourse(
        courseName: String = "Course Name"
    ): CreateCourseRequest {
        val content1 = CreateContentRequest(
            name = "Getting Started",
            description = "Introduction to the course.",
            hours = 1,
            type = ContentType.TOPIC,
            pointsAllotted = 10,
            links = listOf("https://example.com/getting-started-video"),
            files = emptyList()
        )

        val content2 = CreateContentRequest(
            name = "Variables and Data Types",
            description = "Learn about variables and data types in programming.",
            hours = 2,
            type = ContentType.TOPIC,
            pointsAllotted = 10,
            links = emptyList(),
            files = emptyList()
        )

        val content3 = CreateContentRequest(
            name = "Control Structures",
            description = "Understand if-else and loops.",
            hours = 3,
            type = ContentType.TOPIC,
            pointsAllotted = 10,
            links = listOf("https://example.com/control-structures-video"),
            files = emptyList()
        )

        val content4 = CreateContentRequest(
            name = "Functions",
            description = "Learn about functions and methods.",
            hours = 2,
            type = ContentType.TOPIC,
            pointsAllotted = 10,
            links = emptyList(),
            files = emptyList()
        )

        val content5 = CreateContentRequest(
            name = "Arrays and Lists",
            description = "Working with collections.",
            hours = 2,
            type = ContentType.TOPIC,
            pointsAllotted = 10,
            links = listOf("https://example.com/arrays-video"),
            files = emptyList()
        )

        return CreateCourseRequest(
            name = courseName,
            description = "Learn the basics of programming.",
            contents = listOf(content1, content2, content3, content4, content5)
        )
    }
}

