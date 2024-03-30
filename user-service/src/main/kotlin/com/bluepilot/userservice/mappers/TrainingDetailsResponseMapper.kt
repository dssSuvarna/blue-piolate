package com.bluepilot.userservice.mappers

import com.bluepilot.entities.TrainingDetails
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.bluepilot.userservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TrainingDetailsResponseMapper @Autowired constructor(val userService: UserService) {
    fun mapToTrainingDetailsResponse(trainingDetails: TrainingDetails): TrainingDetailsResponse {
        return TrainingDetailsResponse(
            trainingDetailsId = trainingDetails.id,
            userId = trainingDetails.userId,
            trainerId = trainingDetails.trainerId,
            domain = trainingDetails.domain,
            userCourses = trainingDetails.courses.map { it.id },
            startedAt = trainingDetails.startedAt,
            completionTime = trainingDetails.completionTime,
            completedAt = trainingDetails.completedAt,
            trainingStatus = trainingDetails.status
        )
    }
}