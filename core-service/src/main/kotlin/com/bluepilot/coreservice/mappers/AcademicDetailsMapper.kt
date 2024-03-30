package com.bluepilot.coreservice.mappers

import com.bluepilot.entities.AcademicDetails
import com.bluepilot.entities.OnboardingContext
import org.springframework.stereotype.Component

@Component
class AcademicDetailsMapper {
    fun toEntity(onBoardedContext: OnboardingContext): AcademicDetails {
        return AcademicDetails(
            tenthPassOutYear = onBoardedContext.tenthPassoutYear!!,
            tenthPercentage = onBoardedContext.tenthPercentage!!,
            tenthInstitute = onBoardedContext.tenthInstitute!!,
            twelfthPassOutYear = onBoardedContext.twelfthPassoutYear!!,
            twelfthCourse = onBoardedContext.twelfthCourse!!,
            twelfthPercentage = onBoardedContext.twelfthPercentage!!,
            twelfthInstitute = onBoardedContext.twelfthInstitute!!,
            degreePassOutYear = onBoardedContext.degreePassoutYear!!,
            degree = onBoardedContext.degree!!,
            degreeCourse = onBoardedContext.degreeCourse!!,
            degreePercentage = onBoardedContext.degreePercentage!!,
            degreeInstitute = onBoardedContext.degreeInstitute!!,
            document = onBoardedContext.academicDetailsDocument!!
        )
    }
}