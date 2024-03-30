package com.bluepilot.coreservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.mappers.SystemResourceMapper
import com.bluepilot.coreservice.mappers.UserMapper
import com.bluepilot.coreservice.mappers.UserSpecification
import com.bluepilot.coreservice.models.requests.*
import com.bluepilot.coreservice.models.responses.BankDetailsResponse
import com.bluepilot.coreservice.models.responses.UserResourceResponse
import com.bluepilot.coreservice.models.responses.UserResponse
import com.bluepilot.coreservice.models.responses.UserUnAssignSystemResourceResponse
import com.bluepilot.coreservice.utils.StatusTransition
import com.bluepilot.entities.BankDetails
import com.bluepilot.entities.TrainingDetails
import com.bluepilot.entities.User
import com.bluepilot.entities.UserDetails
import com.bluepilot.entities.UserResource
import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.Role
import com.bluepilot.enums.UserStatus
import com.bluepilot.enums.SystemResourceStatus
import com.bluepilot.enums.NotificationEventType.WELCOME_ONBOARDED_EMPLOYEE
import com.bluepilot.enums.NotificationEventType.ONBOARDED_EMPLOYEE
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.ErrorMessages.Companion.AUTH_USER_NOT_FOUND
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_TRANSITION
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.OnboardingContextRepository
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.repositories.UserResourceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class UserService @Autowired constructor(
    val authUserRepository: AuthUserRepository,
    val onboardingContextRepository: OnboardingContextRepository,
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository,
    val userMapper: UserMapper,
    val systemResourceService: SystemResourceService,
    val userResourceRepository: UserResourceRepository,
    val s3Service: S3Service,
    val eventService: EventService,
    val trainingDetailsRepository: TrainingDetailsRepository
) {

    @Value("\${config.hr-gmail}")
    private lateinit var hrGmail: String
    fun updatePassword(updatePasswordRequest: UpdatePasswordRequest, token: String) {
        val username = JwtService.extractUsername(token)
        val authUser =
            authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(AUTH_USER_NOT_FOUND)
        if (!passwordEncoder.matches(updatePasswordRequest.oldPassword, authUser.password)) {
            throw Exception("Incorrect Password")
        }
        authUser.password = passwordEncoder.encode(updatePasswordRequest.newPassword)
        val user = userRepository.findUserByAuthUser(username)
        eventService.sendEvent(user!!.id, NotificationEventType.UPDATED_PASSWORD)
    }

    fun registerUser(createUserRequest: CreateUserRequest): UserResponse {
        val onboardedContext = onboardingContextRepository.findById(createUserRequest.onboardingContextId).orElseThrow {
            NotFoundException(NotFoundError())
        }
        val user = userRepository.save(userMapper.toEntity(createUserRequest, onboardedContext))
        onboardedContext.onboardingContextStatus = OnboardingContextStatus.ONBOARDED
        relocateUserDocs(user.userDetails!!, user.employeeCode)
        val notificationEventTypes =
            listOf(
                // TODO need to changed to admin email
                Pair(ONBOARDED_EMPLOYEE, user.reporter?.userDetails?.professionalEmail?: hrGmail),
                Pair(ONBOARDED_EMPLOYEE, hrGmail),
                Pair(WELCOME_ONBOARDED_EMPLOYEE, user.userDetails!!.professionalEmail),
            )
        sendEmail(notificationEventTypes, user)
        saveTrainingDetails(user)
        return UserResponse(
            id = user.id,
            employeeCode = user.employeeCode,
            firstName = user.firstName,
            lastName = user.lastName,
            designation = user.designation,
            professionalEmail = user.userDetails!!.professionalEmail,
            status = user.status,
            profilePicture = user.profilePicture
        )
    }

    fun saveTrainingDetails(user: User) {
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = user.id,
                trainerId = user.reporter?.id ?: userRepository.findUserByAuthUser_Role_Name(Role.ADMIN).first().id
            )
        )
    }

    fun addBankDetails(userId: Long, createBankDetailsRequest: CreateBankDetailsRequest): BankDetailsResponse {
        val user = getUserById(userId)
        user.userDetails!!.bankDetails = BankDetails(
            accountNumber = createBankDetailsRequest.accountNumber,
            ifsc = createBankDetailsRequest.ifsc,
            bankName = createBankDetailsRequest.bankName,
            accountHolderName = createBankDetailsRequest.accountHolderName
        )
        return BankDetailsResponse(
            accountHolderName = user.userDetails!!.bankDetails!!.accountHolderName,
            accountNumber = user.userDetails!!.bankDetails!!.accountNumber,
            ifsc = user.userDetails!!.bankDetails!!.ifsc,
            bankName = user.userDetails!!.bankDetails!!.bankName
        )
    }

    fun getAllUsersWithFilter(
        pageNumber: Int,
        pageSize: Int,
        userRequestFilter: UserRequestFilter
    ): PageResponse<UserResponse> {
        val pageReq = PageRequest.of(pageNumber, pageSize)
        val spec = UserSpecification.withFilter(userRequestFilter)
        val pageResponse = userRepository.findAll(spec, pageReq)
        return getUserResponse(pageResponse)
    }

    fun getUserResponse(pageOfUsers: Page<User>): PageResponse<UserResponse> {
        return PageResponse(
            totalCount = pageOfUsers.totalElements,
            pageNumber = pageOfUsers.pageable.pageNumber,
            pageSize = pageOfUsers.size,
            currentPageSize = pageOfUsers.pageable.pageSize,
            contents = pageOfUsers.content.map {
                UserResponse(
                    id = it.id,
                    employeeCode = it.employeeCode,
                    designation = it.designation,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    professionalEmail = it.userDetails!!.professionalEmail,
                    status = it.status,
                    profilePicture = it.profilePicture
                )
            }
        )
    }

    fun changeStatus(status: UserStatus, userId: Long) {
        val user = userRepository.findById(userId)
        if (user.isEmpty) {
            throw NotFoundException(UserNotFound())
        }
        val currentStatus = user.get().status
        if (StatusTransition.userStatusTransition[currentStatus]!!.contains(status)) {
            user.get().status = status
            if(status == UserStatus.DEACTIVE)
                user.get().authUser.status = AuthUserStatus.DISABLED
        } else {
            throw NotAllowedException(
                error = NotAllowed(
                    message = "$INVALID_TRANSITION can't move $currentStatus to $status"
                )
            )
        }
    }

    fun updateResourceForUser(request: UpdateUserResourceRequest): UserResourceResponse {
        val user = getUserById(request.userId)
        val systemResources = systemResourceService.getSystemResourceById(request.systemResourceId)
        if (user.resource == null) {
            if (systemResources.status == SystemResourceStatus.ASSIGNED)
                throw NotAllowedException(NotAllowed(message = ErrorMessages.SYSTEM_RESOURCE_IS_ASSIGNED))
            user.resource = UserResource(
                idCard = request.idCard,
                professionalEmail = request.professionalEmail,
                systemResource = systemResources,
                userId = user.id
            )

        } else {
            if (user.resource!!.systemResource == null) {
                user.resource!!.systemResource = systemResources
            }
            else if (user.resource!!.systemResource!!.id != systemResources.id) {
                val oldSystemResource = systemResourceService.getSystemResourceById(user.resource!!.systemResource!!.id)
                oldSystemResource.apply { status = SystemResourceStatus.UNASSIGNED }
                    .takeIf { systemResources.status == SystemResourceStatus.UNASSIGNED } ?: throw NotAllowedException(
                    NotAllowed(message = ErrorMessages.SYSTEM_RESOURCE_IS_ASSIGNED)
                )
            }
            user.resource!!.idCard = request.idCard
            user.resource!!.professionalEmail = request.professionalEmail
            user.resource!!.systemResource = systemResources
        }
        user.userDetails!!.professionalEmail = request.professionalEmail
        user.authUser.username = request.professionalEmail
        systemResources.apply { status = SystemResourceStatus.ASSIGNED }
        userRepository.save(user)

        return UserResourceResponse(
            id = user.resource!!.id,
            idCard = user.resource!!.idCard,
            professionalEmail = user.resource!!.professionalEmail,
            systemResourcesResponse = SystemResourceMapper.mapToResponse(user.resource!!.systemResource!!)
        )
    }

    fun unAssignSystemResourceFromUser(request: UnAssignUserSystemResourceRequest): UserUnAssignSystemResourceResponse {
        val user = getUserById(request.userId)
        val systemResource = systemResourceService.getSystemResourceById(request.systemResourceId)
        if (systemResource.id == user.resource?.systemResource?.id) {
            user.resource?.systemResource!!.status = SystemResourceStatus.UNASSIGNED
            user.resource?.systemResource = null
        } else {
            throw NotAllowedException(NotAllowed())
        }
        return UserUnAssignSystemResourceResponse(
            id = systemResource.id,
            systemId = systemResource.systemId,
            message = "System successfully unassigned from ${user.firstName} ${user.lastName}"
        )
    }

    fun fetchUserResourceByUserId(userId: Long): UserResourceResponse {
        val userResource = userResourceRepository.findByUserId(userId) ?: throw NotFoundException(NotFoundError())
        return UserResourceResponse(
            id = userResource.id,
            idCard = userResource.idCard,
            professionalEmail = userResource.professionalEmail,
            systemResourcesResponse = SystemResourceMapper.mapToResponse(userResource.systemResource)
        )
    }

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { throw NotFoundException(UserNotFound(message = ErrorMessages.USER_NOT_FOUND)) }
    }

    fun getUserFromToken(token: String): User {
        return userRepository.findUserByAuthUser(JwtService.extractUsername(token.substring(7)))!!
    }

    fun relocateUserDocs(userDetails: UserDetails, employeeCode: String) {
        val newPath = "employee-docs/$employeeCode"
        userDetails.apply {
            this.adhaarDoc = s3Service.moveFileToDifferentFolder(this.adhaarDoc, newPath)
            this.panDoc = s3Service.moveFileToDifferentFolder(this.panDoc, newPath)
            this.photo = s3Service.moveFileToDifferentFolder(this.photo, newPath)
            this.academicDetails.document = s3Service.moveFileToDifferentFolder(this.academicDetails.document, newPath)
        }
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    fun updateSaturdayOffByUserId(userId: Long, saturdayOff: Boolean) =
        getUserById(userId).userDetails!!.apply { this.saturdayOff = saturdayOff }

    fun findAll(
        pageNumber: Int,
        pageSize: Int,
        userRequestFilter: UserRequestFilter
    ): PageResponse<User> {
        val pageReq = PageRequest.of(pageNumber, pageSize)
        val spec = UserSpecification.withFilter(userRequestFilter)
        val pageOfUsers = userRepository.findAll(spec, pageReq)
        return PageResponse(
            totalCount = pageOfUsers.totalElements,
            pageNumber = pageOfUsers.pageable.pageNumber,
            pageSize = pageOfUsers.pageable.pageSize,
            currentPageSize = pageOfUsers.numberOfElements,
            contents = pageOfUsers.content
        )
    }

    fun updateProfilePicture(token: String, profilePicture: MultipartFile) {
        val user = getUserFromToken(token)
        user.profilePicture?.let { s3Service.deleteFile(it) }
        user.profilePicture = s3Service.uploadFile(profilePicture)
    }

    fun sendEmail(notificationEventTypes: List<Pair<NotificationEventType, String?>>, user: User) {
        notificationEventTypes.forEach {
            eventService.sendEvent(
                notificationEventType = it.first,
                additionalData = mapOf(
                    "emailTo" to it.second,
                    "receiptName" to if (it.second == hrGmail) "HR" else user.reporter!!.firstName + " " + user.reporter!!.lastName,
                    "employeeName" to user.firstName + " " + user.lastName,
                    "reporterName" to user.reporter!!.firstName + " " + user.reporter!!.lastName,
                    "joiningDate" to user.userDetails!!.dateOfJoining.toString(),
                    "employeeEmail" to user.userDetails!!.professionalEmail,
                    "employeePhone" to user.userDetails!!.contactNumber.toString(),
                    "employeePosition" to user.designation
                )
            )
        }
    }
}