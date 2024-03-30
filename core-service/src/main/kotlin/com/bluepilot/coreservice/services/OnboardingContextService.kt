package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.mappers.OnboardingContextMapper
import com.bluepilot.coreservice.models.requests.BasicDetailsRequest
import com.bluepilot.coreservice.models.responses.BasicDetailsResponse
import com.bluepilot.coreservice.models.responses.OnboardingContextResponse
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.NotificationEventType.INVITE_USER
import com.bluepilot.enums.NotificationEventType.RE_INVITE_USER
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_EMAIL
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_INVITE_CODE
import com.bluepilot.errors.InviteError
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.InvalidInviteException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.OnboardingContextRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@Transactional
class OnboardingContextService @Autowired constructor(
    val onboardingContextRepository: OnboardingContextRepository,
    val onboardingContextMapper: OnboardingContextMapper,
    val eventService: EventService
) {

    @Value("\${config.invite-code-prefix}")
    private lateinit var inviteCodePrefix: String

    fun saveInviteDetails(email: String, name: String): OnboardingContext {
        return onboardingContextRepository.save(
            OnboardingContext(
                personalEmail = email,
                inviteCode = generateInviteCode(),
                onboardingContextStatus = OnboardingContextStatus.INVITED,
                firstName = name
            )
        )
    }

    fun saveBasicDetails(basicDetailsRequest: BasicDetailsRequest): OnboardingContextResponse {
        val foundUser = onboardingContextRepository.findById(basicDetailsRequest.id).orElseThrow {
            throw NotFoundException(NotFoundError())
        }
        val onboardingContext =
            onboardingContextMapper.map(basicDetailsRequest, foundUser)
        return OnboardingContextResponse(
            onboardingContextId = onboardingContext.id,
            onboardingContext.firstName!!,
            onboardingContext.personalEmail,
            onboardingContext.onboardingContextStatus,
            onboardingContext.inviteCode
        )
    }

    fun inviteUser(email: String, name: String, comment: String? = null): String {
        val onBoardingContext = onboardingContextRepository.findByPersonalEmail(email)
        val (eventType, inviteCode) =
            if (onBoardingContext == null) Pair(
                INVITE_USER,
                saveInviteDetails(email, name).inviteCode
            ) else Pair(
                RE_INVITE_USER,
                onBoardingContext.inviteCode
            )

        val response = if (eventType == INVITE_USER) "Invited" else "Re-Invited"
        if (eventType == INVITE_USER) {
            sendNotificationEvent(eventType = eventType, email = email, inviteCode = inviteCode, name = name, comment = comment)
            return response
        }
        if (onBoardingContext!!.onboardingContextStatus == OnboardingContextStatus.TO_BE_VERIFIED) {
            onBoardingContext.onboardingContextStatus = OnboardingContextStatus.RE_INVITED
            sendNotificationEvent(eventType = eventType, email = email, inviteCode = inviteCode, name = name, comment = comment)
            return response
        } else {
            throw InvalidInviteException(
                InviteError(
                    message = ErrorMessages.INVITE_ERROR.replace(
                        "{status}", onBoardingContext.onboardingContextStatus.name
                    )
                )
            )
        }
    }

    fun generateInviteCode(length: Int = 6): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return "${inviteCodePrefix}${
            (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }"
    }

    fun validateInviteCodeAndGetContext(inviteCode: String, email: String): BasicDetailsResponse {
        val onboardingContext = onboardingContextRepository.findByPersonalEmail(email)
            ?: throw NotFoundException(UserNotFound(message = INVALID_EMAIL))
        if (onboardingContext.inviteCode != inviteCode) {
            throw InvalidInviteException(
                InviteError(
                    statusCode = HttpStatus.NOT_ACCEPTABLE,
                    message = INVALID_INVITE_CODE
                )
            )
        }
        return onboardingContextMapper.map(onboardingContext)
    }

    fun updateOnboardingContextStatus(onboardingContextId: Long, status: String) {
        val foundUser = onboardingContextRepository.findById(onboardingContextId).orElseThrow {
            throw NotFoundException(NotFoundError())
        }
        foundUser.onboardingContextStatus = OnboardingContextStatus.valueOf(status)
    }

    fun getAllOnboardingContexts(pageNumber: Int, pageSize: Int): PageResponse<OnboardingContextResponse> {
        val page = onboardingContextRepository
            .findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")))
        return PageResponse(
            totalCount = page.totalElements,
            pageSize = page.size,
            pageNumber = page.pageable.pageNumber,
            contents = page.content.map {
                OnboardingContextResponse(
                    onboardingContextId = it.id,
                    firstName = it.firstName!!,
                    onboardingStatus = it.onboardingContextStatus,
                    personalMail = it.personalEmail,
                    inviteCode = it.inviteCode
                )
            },
            currentPageSize = page.pageable.pageSize
        )
    }

    fun sendNotificationEvent(
        eventType: NotificationEventType,
        email: String,
        name: String,
        inviteCode: String,
        comment: String?,
    ) {
        eventService.sendEvent(
            notificationEventType = eventType,
            additionalData = mapOf(
                "emailTo" to email,
                "employeeName" to name,
                "inviteLink" to "http://localhost:4200/inviteEmployee",
                "inviteCode" to inviteCode,
                "body" to comment
            )
        )
    }
}