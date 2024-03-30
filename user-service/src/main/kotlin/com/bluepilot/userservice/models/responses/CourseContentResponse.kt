package com.bluepilot.userservice.models.responses

import com.bluepilot.enums.ContentType
import com.fasterxml.jackson.annotation.JsonProperty

data class CourseResponse(
    @JsonProperty("courseId")
    val courseId: Long = 0,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("contents")
    val contents: List<ContentResponse>? = emptyList(),
    @JsonProperty("hours")
    val hours: Int,
    @JsonProperty("createdBy")
    val createdBy: String
)

data class ContentResponse(
    @JsonProperty("contentId")
    val contentId: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("hours")
    val hours: Int,
    @JsonProperty("type")
    val type: ContentType,
    @JsonProperty("pointsAllotted")
    val pointsAllotted: Int?,
    @JsonProperty("links")
    val links: List<String>?,
    @JsonProperty("files")
    val files: List<String>?,
    @JsonProperty("createdBy")
    val createdBy: String
)
