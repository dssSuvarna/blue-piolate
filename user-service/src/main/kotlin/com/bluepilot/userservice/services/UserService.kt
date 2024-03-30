package com.bluepilot.userservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.ESIAndPFDetails
import com.bluepilot.entities.User
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages.Companion.ESI_PF_DETAILS_NOT_FOUND
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.errors.UnauthorizeError
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.exceptions.UnauthorizedException
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.UserRepository
import com.bluepilot.userservice.mappers.ESIAndPFResponseMapper
import com.bluepilot.userservice.mappers.UserDetailedMapper
import com.bluepilot.userservice.models.requests.UpdateESIAndPFDetailsByHRRequest
import com.bluepilot.userservice.models.requests.UpdateESIAndPFDetailsByUserRequest
import com.bluepilot.userservice.models.requests.UpdateUserDetailsRequest
import com.bluepilot.userservice.models.responses.EmployeeSummary
import com.bluepilot.userservice.models.responses.UserAcademicDetailsResponse
import com.bluepilot.userservice.models.responses.UserBankDetailsResponse
import com.bluepilot.userservice.models.responses.UserDetailedResponse
import com.bluepilot.userservice.models.responses.UserESIAndPFDetailsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService @Autowired constructor(
    val userRepository: UserRepository,
    val userDetailedMapper: UserDetailedMapper,
    val esiAndPFResponseMapper: ESIAndPFResponseMapper,
    val eventService: EventService
) {
    @Value("\${config.esi.hr-gmail}")
    private lateinit var hrGmail: String
    fun getUserDetailedResponseByUserId(userId: Long): UserDetailedResponse =
        userDetailedMapper.mapUserDetails(getUserById(userId))
    
    fun getUserById(userId: Long): User = 
        userRepository.findById(userId).orElseThrow { NotFoundException(UserNotFound()) }

    fun updateUserDetails(updateUserDetailsRequest: UpdateUserDetailsRequest, token: String): UserDetailedResponse {
        val username = JwtService.extractUsername(token)
        val user = userRepository.findUserByAuthUser(username) ?: throw NotFoundException(UserNotFound())
        return userDetailedMapper.mapUserDetails(updateUserDetails(user, updateUserDetailsRequest))
    }

    private fun updateUserDetails(user: User, updateUserDetailsRequest: UpdateUserDetailsRequest): User {
        user.firstName = updateUserDetailsRequest.firstName
        user.lastName = "${updateUserDetailsRequest.middleName ?: ""} ${updateUserDetailsRequest.lastName}"
        user.userDetails!!.apply {
            contactNumber = updateUserDetailsRequest.contactNumber
            alternateContactNumber = updateUserDetailsRequest.alternateContactNumber
            alternateContactRelation = updateUserDetailsRequest.alternateContactRelation
            localAddress.houseNumber = updateUserDetailsRequest.localAddressHouseNumber
            localAddress.street = updateUserDetailsRequest.localAddressStreet
            localAddress.area = updateUserDetailsRequest.localAddressArea
            localAddress.city = updateUserDetailsRequest.localAddressCity
            localAddress.district = updateUserDetailsRequest.localAddressDistrict
            localAddress.state = updateUserDetailsRequest.localAddressState
            localAddress.pincode = updateUserDetailsRequest.localAddressPincode
            permanentAddress.houseNumber = updateUserDetailsRequest.permanentAddressHouseNumber
            permanentAddress.street = updateUserDetailsRequest.permanentAddressStreet
            permanentAddress.area = updateUserDetailsRequest.permanentAddressArea
            permanentAddress.city = updateUserDetailsRequest.permanentAddressCity
            permanentAddress.district = updateUserDetailsRequest.permanentAddressDistrict
            permanentAddress.state = updateUserDetailsRequest.permanentAddressState
            permanentAddress.pincode = updateUserDetailsRequest.permanentAddressPincode
            personalEmail = updateUserDetailsRequest.personalEmail
            bloodGroup = updateUserDetailsRequest.bloodGroup
        }
        return user
    }

    fun getBankDetails(userId: Long, token: String): UserBankDetailsResponse {
        val authRole = JwtService.extractRole(token)
        // User role is ADMIN or HR return bank details of user
        return if (authRole.name == Role.ADMIN || authRole.name == Role.HR) {
            getBankDetailsOfUser(userId)
        } else {
            val username = JwtService.extractUsername(token)
            val user = userRepository.findById(userId)
            if (!user.isPresent) {
                throw NotFoundException(UserNotFound())
            }
            // Validating requested user can view only his bank details
            if (user.get().authUser.username == username && (authRole.name == Role.EMPLOYEE)) {
                userDetailedMapper.mapUserBankDetails(user.get())
            } else {
                throw UnauthorizedException(UnauthorizeError())
            }
        }
    }

    fun getAcademicDetails(userId: Long, token: String): UserAcademicDetailsResponse {
        val authRole = JwtService.extractRole(token)
        // User role is ADMIN or HR return academic details of user
        return if (authRole.name == Role.ADMIN || authRole.name == Role.HR) {
            getAcademicDetailsOfUser(userId)
        } else {
            val username = JwtService.extractUsername(token)
            val user = userRepository.findById(userId)
            if (!user.isPresent) {
                throw NotFoundException(UserNotFound())
            }
            // Validating requested user can view only his academic details
            if (user.get().authUser.username == username && (authRole.name == Role.EMPLOYEE)) {
                userDetailedMapper.mapUserAcademicDetails(user.get())
            } else {
                throw UnauthorizedException(UnauthorizeError())
            }
        }
    }

    private fun getBankDetailsOfUser(userId: Long): UserBankDetailsResponse {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            return userDetailedMapper.mapUserBankDetails(user.get())
        } else {
            throw NotFoundException(UserNotFound())
        }
    }

    private fun getAcademicDetailsOfUser(userId: Long): UserAcademicDetailsResponse {
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            return userDetailedMapper.mapUserAcademicDetails(user.get())
        } else {
            throw NotFoundException(UserNotFound())
        }
    }

    fun saveESIAndPFDetailsByUser(
        updateESIAndPFDetailsByUserRequest: UpdateESIAndPFDetailsByUserRequest
    ): UserESIAndPFDetailsResponse {
        val user = userRepository.findById(updateESIAndPFDetailsByUserRequest.userId)
        if (!user.isPresent) throw NotFoundException(UserNotFound())
        user.get().userDetails!!.esiAndPFDetails = updateESIAndPFDetailsByUser(
            updateESIAndPFDetailsByUserRequest, user.get().userDetails!!.esiAndPFDetails
        )
        //send notification to hr
        eventService.sendEvent(
            notificationEventType = NotificationEventType.ESI_AND_PF_UPDATE,
            additionalData = mapOf(
                "emailTo" to hrGmail,
                "employeeName" to (user.get().firstName +" " + user.get().lastName).uppercase(),
            )
        )
        return esiAndPFResponseMapper.mapESIAndPFResponse(user.get().userDetails!!.esiAndPFDetails!!)
    }

    fun updateESIAndPFDetailsByHR(updateESIAndPFDetailsByHRRequest: UpdateESIAndPFDetailsByHRRequest): UserESIAndPFDetailsResponse {
        val user = userRepository.findById(updateESIAndPFDetailsByHRRequest.userId)
        if (!user.isPresent) throw NotFoundException(UserNotFound())
        user.get().userDetails!!.esiAndPFDetails = updateESIAndPFDetailsByHR(
            updateESIAndPFDetailsByHRRequest, user.get().userDetails!!.esiAndPFDetails
        )
        return esiAndPFResponseMapper.mapESIAndPFResponse(user.get().userDetails!!.esiAndPFDetails!!)

    }

    fun getESIAndPFDetailsByUserId(userId: Long): UserESIAndPFDetailsResponse {
        val foundUser = userRepository.findById(userId).orElseThrow { NotFoundException(UserNotFound()) }
        val esiAndPFDetails = foundUser.userDetails!!.esiAndPFDetails
            .takeIf { it != null } ?: throw NotFoundException(ResourceNotFound(message = ESI_PF_DETAILS_NOT_FOUND))
            return esiAndPFResponseMapper.mapESIAndPFResponse(esiAndPFDetails)
    }

    fun getESIAndPFDetailsForUser(token: String): UserESIAndPFDetailsResponse {
         val user = getUserFromToken(token)
        return getESIAndPFDetailsByUserId(user.id)
    }

    fun getUserFromToken(token:String): User {
        return userRepository
            .findUserByAuthUser(JwtService.extractUsername(token.substring(7))) ?: throw NotFoundException(UserNotFound())
    }

    fun userIsAdminOrHR(user: User) = listOf(Role.ADMIN, Role.HR).contains(user.getUserRole())


    private fun updateESIAndPFDetailsByUser(
        updateESIAndPFDetailsByUserRequest: UpdateESIAndPFDetailsByUserRequest,
        esiAndPFDetail: ESIAndPFDetails?
    ): ESIAndPFDetails {
        val esiAndPFDetails = esiAndPFDetail ?: ESIAndPFDetails()
        esiAndPFDetails.adhaarName = updateESIAndPFDetailsByUserRequest.adhaarName
        esiAndPFDetails.gender = updateESIAndPFDetailsByUserRequest.gender
        esiAndPFDetails.maritalStatus = updateESIAndPFDetailsByUserRequest.maritalStatus
        esiAndPFDetails.empDob = updateESIAndPFDetailsByUserRequest.empDob
        esiAndPFDetails.mobNo = updateESIAndPFDetailsByUserRequest.mobNo
        esiAndPFDetails.fatherOrHusbandName = updateESIAndPFDetailsByUserRequest.fatherOrHusbandName
        esiAndPFDetails.relWithEmp = updateESIAndPFDetailsByUserRequest.relWithEmp
        esiAndPFDetails.adhaarNo = updateESIAndPFDetailsByUserRequest.adhaarNo
        esiAndPFDetails.panNo = updateESIAndPFDetailsByUserRequest.panNo
        esiAndPFDetails.flatOrHouseNo = updateESIAndPFDetailsByUserRequest.flatOrHouseNo
        esiAndPFDetails.streetNo = updateESIAndPFDetailsByUserRequest.streetNo
        esiAndPFDetails.landMark = updateESIAndPFDetailsByUserRequest.landMark
        esiAndPFDetails.state = updateESIAndPFDetailsByUserRequest.state
        esiAndPFDetails.dist = updateESIAndPFDetailsByUserRequest.dist
        esiAndPFDetails.fatherName = updateESIAndPFDetailsByUserRequest.fatherName
        esiAndPFDetails.adhaarCardOfFather = updateESIAndPFDetailsByUserRequest.adhaarCardOfFather
        esiAndPFDetails.dobOfFather = updateESIAndPFDetailsByUserRequest.dobOfFather
        esiAndPFDetails.motherName = updateESIAndPFDetailsByUserRequest.motherName
        esiAndPFDetails.adhaarCardOfMother = updateESIAndPFDetailsByUserRequest.adhaarCardOfMother
        esiAndPFDetails.dobOfMother = updateESIAndPFDetailsByUserRequest.dobOfMother
        esiAndPFDetails.wifeName = updateESIAndPFDetailsByUserRequest.wifeName
        esiAndPFDetails.adhaarCardOfWife = updateESIAndPFDetailsByUserRequest.adhaarCardOfWife
        esiAndPFDetails.dobOfWife = updateESIAndPFDetailsByUserRequest.dobOfWife
        esiAndPFDetails.childOne = updateESIAndPFDetailsByUserRequest.childOne
        esiAndPFDetails.adhaarCardOfChildOne = updateESIAndPFDetailsByUserRequest.adhaarCardOfChildOne
        esiAndPFDetails.genderOfChildOne = updateESIAndPFDetailsByUserRequest.genderOfChildOne
        esiAndPFDetails.dobOfChildOne = updateESIAndPFDetailsByUserRequest.dobOfChildOne
        esiAndPFDetails.childTwo = updateESIAndPFDetailsByUserRequest.childTwo
        esiAndPFDetails.adhaarCardOfChildTwo = updateESIAndPFDetailsByUserRequest.adhaarCardOfChildTwo
        esiAndPFDetails.genderOfChildTwo = updateESIAndPFDetailsByUserRequest.genderOfChildTwo
        esiAndPFDetails.dobOfChildTwo = updateESIAndPFDetailsByUserRequest.dobOfChildTwo
        esiAndPFDetails.childThree = updateESIAndPFDetailsByUserRequest.childThree
        esiAndPFDetails.adhaarCardOfChildThree = updateESIAndPFDetailsByUserRequest.adhaarCardOfChildThree
        esiAndPFDetails.genderOfChildThree = updateESIAndPFDetailsByUserRequest.genderOfChildThree
        esiAndPFDetails.dobOfChildThree = updateESIAndPFDetailsByUserRequest.dobOfChildThree
        esiAndPFDetails.childFour = updateESIAndPFDetailsByUserRequest.childFour
        esiAndPFDetails.adhaarCardOfChildFour = updateESIAndPFDetailsByUserRequest.adhaarCardOfChildFour
        esiAndPFDetails.genderOfChildFour = updateESIAndPFDetailsByUserRequest.genderOfChildFour
        esiAndPFDetails.dobOfChildFour = updateESIAndPFDetailsByUserRequest.dobOfChildFour
        esiAndPFDetails.childFive = updateESIAndPFDetailsByUserRequest.childFive
        esiAndPFDetails.adhaarCardOfChildFive = updateESIAndPFDetailsByUserRequest.adhaarCardOfChildFive
        esiAndPFDetails.genderOfChildFive = updateESIAndPFDetailsByUserRequest.genderOfChildFive
        esiAndPFDetails.dobOfChildFive = updateESIAndPFDetailsByUserRequest.dobOfChildFive
        esiAndPFDetails.nominee = updateESIAndPFDetailsByUserRequest.nominee
        return esiAndPFDetails
    }

    private fun updateESIAndPFDetailsByHR(
        updateESIAndPFDetailsByHRRequest: UpdateESIAndPFDetailsByHRRequest,
        esiAndPFDetail: ESIAndPFDetails?
    ): ESIAndPFDetails {
        val esiAndPFDetails = esiAndPFDetail ?: ESIAndPFDetails()

        esiAndPFDetails.empCode = updateESIAndPFDetailsByHRRequest.empCode
        esiAndPFDetails.uanNo = updateESIAndPFDetailsByHRRequest.uanNo
        esiAndPFDetails.pfNoOrPfMemberId = updateESIAndPFDetailsByHRRequest.pfNoOrPfMemberId
        esiAndPFDetails.esicNo = updateESIAndPFDetailsByHRRequest.esicNo
        esiAndPFDetails.empDoj = updateESIAndPFDetailsByHRRequest.empDoj
        esiAndPFDetails.pf = updateESIAndPFDetailsByHRRequest.pf
        esiAndPFDetails.esi = updateESIAndPFDetailsByHRRequest.esi
        esiAndPFDetails.pt = updateESIAndPFDetailsByHRRequest.pt
        esiAndPFDetails.email = updateESIAndPFDetailsByHRRequest.email
        esiAndPFDetails.nationality = updateESIAndPFDetailsByHRRequest.nationality
        esiAndPFDetails.bankAccountNo = updateESIAndPFDetailsByHRRequest.bankAccountNo
        esiAndPFDetails.bankName = updateESIAndPFDetailsByHRRequest.bankName
        esiAndPFDetails.ifscCode = updateESIAndPFDetailsByHRRequest.ifscCode
        esiAndPFDetails.salaryCategory = updateESIAndPFDetailsByHRRequest.salaryCategory
        esiAndPFDetails.grossSalaryOfFullMonth = updateESIAndPFDetailsByHRRequest.grossSalaryOfFullMonth
        esiAndPFDetails.basic = updateESIAndPFDetailsByHRRequest.basic
        esiAndPFDetails.hra = updateESIAndPFDetailsByHRRequest.hra
        esiAndPFDetails.conveyAllow = updateESIAndPFDetailsByHRRequest.conveyAllow
        esiAndPFDetails.cityCompAllow = updateESIAndPFDetailsByHRRequest.cityCompAllow
        esiAndPFDetails.medAllow = updateESIAndPFDetailsByHRRequest.medAllow
        esiAndPFDetails.eduAllow = updateESIAndPFDetailsByHRRequest.eduAllow
        esiAndPFDetails.transport = updateESIAndPFDetailsByHRRequest.transport
        esiAndPFDetails.tea = updateESIAndPFDetailsByHRRequest.tea
        esiAndPFDetails.mobileAllow = updateESIAndPFDetailsByHRRequest.mobileAllow
        esiAndPFDetails.newsPaper = updateESIAndPFDetailsByHRRequest.newsPaper
        esiAndPFDetails.hostelAllow = updateESIAndPFDetailsByHRRequest.hostelAllow
        esiAndPFDetails.washingAllow = updateESIAndPFDetailsByHRRequest.washingAllow
        esiAndPFDetails.foodAllow = updateESIAndPFDetailsByHRRequest.foodAllow
        esiAndPFDetails.total = updateESIAndPFDetailsByHRRequest.total
        esiAndPFDetails.remark = updateESIAndPFDetailsByHRRequest.remark
        return esiAndPFDetails
    }

    fun getAllUsersSummary(): List<EmployeeSummary> {
        return userRepository.findAll().map {
            EmployeeSummary(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                employeeCode = it.employeeCode,
                designation = it.designation,
                email = it.userDetails?.professionalEmail,
                trainer = it.reporter?.id,
                profilePicture = it.profilePicture
            )
        }
    }
}