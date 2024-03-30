package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.repositories.UserRepository
import com.bluepilot.entities.User
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.UserStatus
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_TRANSITION
import com.bluepilot.errors.ErrorMessages.Companion.REPORTER_MUST_BE_ASSIGNED
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UserMapper @Autowired constructor(
    val authUserMapper: AuthUserMapper,
    val userDetailsMapper: UserDetailsMapper,
    val userRepository: UserRepository
) {
    @Value("\${config.employee-code-prefix}")
    val employeeCodePrefix: String = ""

    @Value("\${config.employee-code-buffer}")
    val employeeCodeBuffer: Long = 0L

    fun toEntity(createUserRequest: CreateUserRequest, onboardedContext: OnboardingContext): User {
        return User(
            firstName = onboardedContext.firstName!!,
            lastName = "${onboardedContext.middleName ?: ""} ${onboardedContext.lastName}",
            employeeCode = getEmployeeCode(),
            designation = createUserRequest.designation,
            authUser = authUserMapper.toEntity(createUserRequest),
            status = UserStatus.ONBOARDED.takeIf {
                onboardedContext.onboardingContextStatus == OnboardingContextStatus.APPROVED
            } ?: throw NotAllowedException(NotAllowed(message = INVALID_TRANSITION)),
            userDetails = userDetailsMapper.toEntity(createUserRequest, onboardedContext),
            reporter = if (createUserRequest.reporterId == null) {
                throw NotAllowedException(NotAllowed(message = REPORTER_MUST_BE_ASSIGNED))
            } else {
                userRepository.findById(createUserRequest.reporterId).orElseThrow { NotFoundException(UserNotFound()) }
            }
        )
    }

    fun getEmployeeCode(): String = "$employeeCodePrefix${userRepository.count() + employeeCodeBuffer}"
}