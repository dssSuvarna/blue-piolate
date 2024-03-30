package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.coreservice.services.LeaveDetailsService
import com.bluepilot.entities.LeaveDetails
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.entities.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserDetailsMapper @Autowired constructor(
    val academicDetailsMapper: AcademicDetailsMapper,
    val leaveDetailsService: LeaveDetailsService
) {
    fun toEntity(createUserRequest: CreateUserRequest, onboardingContext: OnboardingContext): UserDetails {
        return UserDetails(
            professionalEmail = createUserRequest.professionalEmail,
            personalEmail = onboardingContext.personalEmail,
            alternateContactNumber = onboardingContext.alternateContactNumber!!,
            alternateContactRelation = onboardingContext.alternateContactRelation!!,
            contactNumber = onboardingContext.contactNumber!!,
            dateOfJoining = createUserRequest.dateOfJoining,
            adhaarNumber = onboardingContext.aadhaarNumber!!,
            dateOfBirth = onboardingContext.dateOfBirth!!,
            panNumber = onboardingContext.panNumber!!,
            photo = onboardingContext.photo!!,
            adhaarDoc = onboardingContext.aadhaarDocument!!,
            panDoc = onboardingContext.panDocument!!,
            gender = onboardingContext.gender!!,
            bloodGroup = onboardingContext.bloodGroup!!,
            academicDetails = academicDetailsMapper.toEntity(onboardingContext),
            bankDetails = null,
            esiAndPFDetails = null,
            localAddress = onboardingContext.localAddress!!,
            permanentAddress = onboardingContext.permanentAddress!!,
            leaveDetails = LeaveDetails(
                totalLeaves = leaveDetailsService.getTotalLeaves(),
                totalSickLeave = leaveDetailsService.getTotalSickLeaves(),
                totalPrivilegeLeave = leaveDetailsService.getTotalPrivilegeLeaves(),
            ),
            salaryDetails = null
        )
    }
}
