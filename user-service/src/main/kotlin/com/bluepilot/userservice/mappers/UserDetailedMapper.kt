package com.bluepilot.userservice.mappers

import com.bluepilot.entities.User
import com.bluepilot.userservice.models.responses.UserAcademicDetailsResponse
import com.bluepilot.userservice.models.responses.UserBankDetailsResponse
import com.bluepilot.userservice.models.responses.UserDetailedResponse
import org.springframework.stereotype.Component

@Component
class UserDetailedMapper {
    fun mapUserDetails(user: User) : UserDetailedResponse { 
        return UserDetailedResponse(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            employeeCode = user.employeeCode,
            designation = user.designation,
            professionalEmail = user.userDetails!!.professionalEmail,
            personalEmail = user.userDetails!!.personalEmail,
            gender = user.userDetails!!.gender,
            contactNumber = user.userDetails!!.contactNumber,
            alternateContactNumber = user.userDetails!!.alternateContactNumber,
            alternateContactRelation = user.userDetails!!.alternateContactRelation,
            dateOfJoining = user.userDetails!!.dateOfJoining,
            dateOfBirth = user.userDetails!!.dateOfBirth,
            bloodGroup = user.userDetails!!.bloodGroup,
            localAddress = user.userDetails!!.localAddress,
            permanentAddress = user.userDetails!!.permanentAddress,
            aadhaarNumber = user.userDetails!!.adhaarNumber,
            panNumber = user.userDetails!!.panNumber,
            reporter = user.reporter!!.firstName,
            status = user.status,
            profilePicture = user.profilePicture,
            salaryDetailsId = user.userDetails?.salaryDetails?.id,
            saturdayOff = user.userDetails!!.saturdayOff
        )
    }

    fun mapUserBankDetails(user: User) : UserBankDetailsResponse {
        return UserBankDetailsResponse(
            accountNumber = user.userDetails!!.bankDetails!!.accountNumber,
            ifsc = user.userDetails!!.bankDetails!!.ifsc,
            bankName = user.userDetails!!.bankDetails!!.bankName,
            accountHolderName = user.userDetails!!.bankDetails!!.accountHolderName
        )
    }

    fun mapUserAcademicDetails(user: User): UserAcademicDetailsResponse {
        return UserAcademicDetailsResponse(
            tenthPassOutYear = user.userDetails!!.academicDetails.tenthPassOutYear,
            tenthPercentage = user.userDetails!!.academicDetails.tenthPercentage,
            tenthInstitute = user.userDetails!!.academicDetails.tenthInstitute,
            twelfthPassOutYear = user.userDetails!!.academicDetails.twelfthPassOutYear,
            twelfthCourse = user.userDetails!!.academicDetails.twelfthCourse,
            twelfthPercentage = user.userDetails!!.academicDetails.twelfthPercentage,
            twelfthInstitute = user.userDetails!!.academicDetails.twelfthInstitute,
            degreePassOutYear = user.userDetails!!.academicDetails.degreePassOutYear,
            degree = user.userDetails!!.academicDetails.degree,
            degreeCourse = user.userDetails!!.academicDetails.degreeCourse,
            degreePercentage = user.userDetails!!.academicDetails.degreePercentage,
            degreeInstitute = user.userDetails!!.academicDetails.degreeInstitute,
            document = user.userDetails!!.academicDetails.document
        )
    }
}