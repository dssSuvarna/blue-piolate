package com.bluepilot.userservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.TrainingDetails
import com.bluepilot.enums.Domain
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages.Companion.INVALID_USER
import com.bluepilot.errors.ErrorMessages.Companion.TRAINING_IS_ALREADY_STARTED
import com.bluepilot.errors.ErrorMessages.Companion.USER_IS_NOT_A_TRAINER
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.UnauthorizedException
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.models.requests.StartTrainingRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
@RunWith(SpringRunner::class)
class TrainingServiceTest @Autowired constructor(
    val roleRepository: AuthRoleRepository,
    val trainingService: TrainingService,
    val trainingDetailsRepository: TrainingDetailsRepository,
    val userGenerator: UserGenerator,
    val userRepository: UserRepository
) : BaseTestConfig() {

    @Test
    fun shouldNotStartTrainingIfTrainingIsInProgress() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val savedUser = userRepository.save(userGenerator.getUser(id = 0L))
        val savedTrainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser.id,
                trainerId = savedUser.reporter!!.id,
                startedAt = Timestamp.from(Instant.now())
            )
        )

        val startTrainingRequest = StartTrainingRequest(
            trainingDetailsId = savedTrainingDetails.id,
            userId = savedUser.id,
            trainerId = savedUser.reporter!!.id,
            domain = Domain.BACKEND,
            completionTime = 45
        )
        val exception = Assertions.assertThrows(NotAllowedException::class.java) {
            trainingService.startTrainingForUser(startTrainingRequest, token)
        }.error

        Assertions.assertEquals(TRAINING_IS_ALREADY_STARTED, exception.message)
    }

    @Test
    fun shouldNotStartTrainingForUnauthorizedTrainer() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val savedUser = userRepository.save(userGenerator.getUser(id = 0L, authRole = employeeRole!!))
        val token = "Bearer ${JwtService.generateToken(savedUser.authUser)}"
        val savedTrainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser.id,
                trainerId = savedUser.reporter!!.id,
            )
        )
        val startTrainingRequest = StartTrainingRequest(
            trainingDetailsId = savedTrainingDetails.id,
            userId = savedUser.id,
            trainerId = savedUser.reporter!!.id,
            domain = Domain.BACKEND,
            completionTime = 45
        )

        val exception = Assertions.assertThrows(NotAllowedException::class.java) {
            trainingService.startTrainingForUser(startTrainingRequest, token)
        }.error

        Assertions.assertEquals(INVALID_USER, exception.message)
    }

    @Test
    fun shouldNotGetListOfTraineesForEmployeeWhoIsNotTrainer() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val trainer = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))

        val trainee1 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole, reporter = trainer))
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = trainee1.id,
                trainerId = trainee1.reporter!!.id
            )
        )

        val trainee2 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test3", authRole = employeeRole, reporter = trainer))
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = trainee2.id,
                trainerId = trainee2.reporter!!.id
            )
        )

        val token = "Bearer ${JwtService.generateToken(trainee1.authUser)}"
        val exception = Assertions.assertThrows(UnauthorizedException::class.java) {
            trainingService.getTraineesForATrainer(token)
        }.error

        Assertions.assertEquals(USER_IS_NOT_A_TRAINER, exception.message)
    }
}