package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.math.BigDecimal


@Entity
@Table(name = "academic_details", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class AcademicDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val tenthPassOutYear: Int,
    val tenthPercentage: BigDecimal,
    val tenthInstitute: String,
    val twelfthPassOutYear: Int,
    val twelfthCourse: String,
    val twelfthPercentage: BigDecimal,
    val twelfthInstitute: String,
    val degreePassOutYear: Int,
    val degree: String,
    val degreeCourse: String,
    val degreePercentage: BigDecimal,
    val degreeInstitute: String,
    var document: String
)
