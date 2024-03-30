package com.bluepilot.userservice.models.requests

data class CreateCourseRequest(
    val name: String,
    val description: String,
    val contents: List<CreateContentRequest>
)
