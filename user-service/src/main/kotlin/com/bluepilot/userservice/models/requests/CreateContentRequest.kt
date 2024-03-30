package com.bluepilot.userservice.models.requests

import com.bluepilot.enums.ContentType

data class CreateContentRequest(
    val name: String,
    val description: String,
    val hours: Int,
    val type: ContentType,
    val pointsAllotted: Int?,
    val links: List<String> = mutableListOf(),
    val files: List<String> = mutableListOf()
)
