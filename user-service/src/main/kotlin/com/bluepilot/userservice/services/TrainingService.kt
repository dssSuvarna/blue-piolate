package com.bluepilot.userservice.services

import com.bluepilot.entities.TrainingDetails
import com.bluepilot.entities.User
import com.bluepilot.enums.TrainingStatus
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_USER
import com.bluepilot.errors.ErrorMessages.Companion.NOT_ALLOWED_TO_UPDATE_TRAINING_DETAILS
import com.bluepilot.errors.ErrorMessages.Companion.TRAINING_DETAILS_NOT_FOUND
import com.bluepilot.errors.ErrorMessages.Companion.TRAINING_IS_ALREADY_COMPLETED
import com.bluepilot.errors.ErrorMessages.Companion.TRAINING_IS_ALREADY_STARTED
import com.bluepilot.errors.ErrorMessages.Companion.USER_IS_NOT_A_TRAINER
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.errors.UnauthorizeError
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.exceptions.UnauthorizedException
import com.bluepilot.exceptions.Validator.Companion.validate
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.userservice.mappers.TrainingDetailsResponseMapper
import com.bluepilot.userservice.mappers.TrainingDetailsSpecification
import com.bluepilot.userservice.models.requests.StartTrainingRequest
import com.bluepilot.userservice.models.requests.TrainingDetailsRequestFilter
import com.bluepilot.userservice.models.requests.UpdateTrainingDetailsRequest
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant

@Service
@Transactional
class TrainingService @Autowired constructor(
    val trainingDetailsRepository: TrainingDetailsRepository,
    val trainingDetailsResponseMapper: TrainingDetailsResponseMapper,
    val userService: UserService
) {
    fun updateTrainingDetails(
        updateTrainingDetailsRequest: UpdateTrainingDetailsRequest, token: String
    ): TrainingDetailsResponse {
        val loggedInUser = userService.getUserFromToken(token)
        val trainingDetails = getTrainingDetailsById(updateTrainingDetailsRequest.trainingDetailsId)
        val isAdminOrHr = userService.userIsAdminOrHR(loggedInUser)
        validate(
            !(isTrainerOfTrainingDetail(loggedInUser, trainingDetails) || isAdminOrHr),
            NotAllowedException(NotAllowed(message = NOT_ALLOWED_TO_UPDATE_TRAINING_DETAILS))
        )
        trainingDetails.apply {
            trainerId = updateTrainingDetailsRequest.trainerId
            domain = updateTrainingDetailsRequest.domain
            completionTime = updateTrainingDetailsRequest.completionTime
                .takeIf { this.startedAt != null } ?: throw NotAllowedException(NotAllowed())
        }
        return trainingDetailsResponseMapper.mapToTrainingDetailsResponse(trainingDetails)
    }

    fun startTrainingForUser(startTrainingRequest: StartTrainingRequest, token: String) {
        val loggedInUser = userService.getUserFromToken(token)
        val trainingDetails = getTrainingDetailsById(startTrainingRequest.trainingDetailsId)
        val isAdminOrHr = userService.userIsAdminOrHR(loggedInUser)
        trainingDetails.also {
            validate(
                !(isTrainerOfTrainingDetail(loggedInUser, it) || isAdminOrHr),
                NotAllowedException(NotAllowed(message = INVALID_USER))
            )
            validate(it.startedAt != null, NotAllowedException(NotAllowed(message = TRAINING_IS_ALREADY_STARTED)))
            validate(it.completedAt != null, NotAllowedException(NotAllowed(message = TRAINING_IS_ALREADY_COMPLETED)))
        }
        trainingDetails.apply {
            trainerId = startTrainingRequest.trainerId
            domain = startTrainingRequest.domain
            startedAt = Timestamp.from(Instant.now())
            completionTime = startTrainingRequest.completionTime
            status = TrainingStatus.IN_PROGRESS
        }
    }

    fun getTrainingDetailsById(trainingDetailsId: Long): TrainingDetails =
        trainingDetailsRepository.findById(trainingDetailsId)
            .orElseThrow { NotFoundException(ResourceNotFound(message = TRAINING_DETAILS_NOT_FOUND)) }

    fun getTrainingDetailsByUserId(userId: Long, token: String): TrainingDetailsResponse {
        val loggedInUser = userService.getUserFromToken(token)
        val trainingDetails = trainingDetailsRepository
            .findByUserId(userId) ?: throw NotFoundException(ResourceNotFound(message = TRAINING_DETAILS_NOT_FOUND))
        val isValidUser = (loggedInUser.id == trainingDetails.userId)
        val isAdminOrHr = userService.userIsAdminOrHR(loggedInUser)
        validate(
            !(isTrainerOfTrainingDetail(loggedInUser, trainingDetails) || isValidUser || isAdminOrHr),
            NotAllowedException(NotAllowed(message = INVALID_USER))
        )
        return trainingDetailsResponseMapper.mapToTrainingDetailsResponse(trainingDetails)
    }

    fun getTraineesForATrainer(token: String): List<TrainingDetailsResponse> {
        val user = userService.getUserFromToken(token)
        val trainees = trainingDetailsRepository.findAllByTrainerId(user.id).takeIf { it.isNotEmpty() }
            ?: throw UnauthorizedException(UnauthorizeError(message = USER_IS_NOT_A_TRAINER))
        return trainees.map { trainingDetailsResponseMapper.mapToTrainingDetailsResponse(it) }
    }

    fun getAllTrainingDetails(
        pageNumber: Int,
        pageSize: Int,
        trainingDetailsRequestFilter: TrainingDetailsRequestFilter
    ): PageResponse<TrainingDetailsResponse> {
        val pageReq = PageRequest.of(pageNumber, pageSize)
        val spec = TrainingDetailsSpecification.withFilter(trainingDetailsRequestFilter)
        val trainingDetails =  trainingDetailsRepository.findAll(spec, pageReq)
        return PageResponse(
            totalCount = trainingDetails.totalElements,
            pageNumber = trainingDetails.pageable.pageNumber,
            pageSize = trainingDetails.size,
            currentPageSize = trainingDetails.pageable.pageSize,
            contents = trainingDetails.content.map {
                trainingDetailsResponseMapper.mapToTrainingDetailsResponse(it)
            }
        )
    }

    fun isTrainerOfTrainingDetail(user: User, trainingDetails: TrainingDetails): Boolean =
        user.id == trainingDetails.trainerId
}