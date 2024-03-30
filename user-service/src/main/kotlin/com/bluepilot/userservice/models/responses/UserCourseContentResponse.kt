package com.bluepilot.userservice.models.responses

import com.bluepilot.enums.ProgressStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class UserCourseResponse(
    @JsonProperty("userCourseId")
    val userCourseId: Long,
    @JsonProperty("course")
    val course: CourseResponse,
    @JsonProperty("trainingDetailsId")
    val trainingDetailsId: Long,
    @JsonProperty("status")
    val status: ProgressStatus,
    @JsonProperty("startedAt")
    val startedAt: Timestamp?,
    @JsonProperty("completedAt")
    val completedAt: Timestamp?,
    @JsonProperty("hoursSpent")
    val hoursSpent: Int?,
    @JsonProperty("userContent")
    val userContent: List<UserContentResponse>
)

data class UserContentResponse(
    @JsonProperty("userContentId")
    val userContentId: Long,
    @JsonProperty("content")
    val content: ContentResponse,
    @JsonProperty("contentStatus")
    val contentStatus: ProgressStatus,
    @JsonProperty("startedAt")
    val startedAt: Timestamp?,
    @JsonProperty("completedAt")
    val completedAt: Timestamp?,
    @JsonProperty("pointsAwarded")
    val pointsAwarded: Int?,
)
