package com.bluepilot.userservice.models.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class UserAcademicDetailsResponse(
    @JsonProperty("tenthPassOutYear")
    val tenthPassOutYear: Int,
    @JsonProperty("tenthPercentage")
    val tenthPercentage: BigDecimal,
    @JsonProperty("tenthInstitute")
    val tenthInstitute: String,
    @JsonProperty("twelfthPassOutYear")
    val twelfthPassOutYear: Int,
    @JsonProperty("twelfthCourse")
    val twelfthCourse: String,
    @JsonProperty("twelfthPercentage")
    val twelfthPercentage: BigDecimal,
    @JsonProperty("twelfthInstitute")
    val twelfthInstitute: String,
    @JsonProperty("degreePassOutYear")
    val degreePassOutYear: Int,
    @JsonProperty("degree")
    val degree: String,
    @JsonProperty("degreeCourse")
    val degreeCourse: String,
    @JsonProperty("degreePercentage")
    val degreePercentage: BigDecimal,
    @JsonProperty("degreeInstitute")
    val degreeInstitute: String,
    @JsonProperty("document")
    val document: String
)
