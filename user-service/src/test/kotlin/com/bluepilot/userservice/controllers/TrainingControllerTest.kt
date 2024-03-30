package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.TrainingDetails
import com.bluepilot.enums.Domain
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.models.requests.StartTrainingRequest
import com.bluepilot.userservice.models.requests.TrainingDetailsRequestFilter
import com.bluepilot.userservice.models.requests.UpdateTrainingDetailsRequest
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import java.sql.Timestamp
import java.time.Instant

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class TrainingControllerTest @Autowired constructor(
    val trainingDetailsRepository: TrainingDetailsRepository,
    val roleRepository: AuthRoleRepository,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldUpdateTrainingDetailsByAdmin() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val savedUser = userRepository.save(userGenerator.getUser(id = 0L))
        val savedTrainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser.id,
                trainerId = savedUser.reporter!!.id,
                startedAt = Timestamp.from(Instant.now()),
                completionTime = 45
            )
        )
        val updateTrainingDetailsRequest = UpdateTrainingDetailsRequest(
            trainingDetailsId = savedTrainingDetails.id,
            trainerId = savedTrainingDetails.trainerId,
            domain = Domain.values().random(),
            completionTime = Int.MAX_VALUE.coerceIn(0, 90)
        )

        val result = mockMvc.put("/admin/training/update") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateTrainingDetailsRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, TrainingDetailsResponse::class.java)

        Assertions.assertEquals(updateTrainingDetailsRequest.domain, response.domain)
        Assertions.assertEquals(updateTrainingDetailsRequest.completionTime, response.completionTime)
    }

    @Test
    fun shouldGetTrainingDetailsByUserId() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val savedUser = userRepository.save(userGenerator.getUser(id = 0L))
        val savedTrainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser.id,
                trainerId = savedUser.reporter!!.id
            )
        )

        val result = mockMvc.get("/admin/training/${savedUser.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, TrainingDetailsResponse::class.java)

        Assertions.assertEquals(response.trainingDetailsId, savedTrainingDetails.id)
        Assertions.assertEquals(response.trainerId, savedTrainingDetails.trainerId)
        Assertions.assertEquals(response.userId, savedTrainingDetails.userId)
    }

    @Test
    fun shouldNotGetTrainingDetailsForUnauthorizedUser() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val savedUser1 = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser1.id,
                trainerId = savedUser1.reporter!!.id
            )
        )

        val savedUser2 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole))
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser2.id,
                trainerId = savedUser2.reporter!!.id
            )
        )

        val tokenOfUnauthorizedUser = "Bearer ${JwtService.generateToken(savedUser2.authUser)}"

        mockMvc.get("/admin/training/${savedUser1.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", tokenOfUnauthorizedUser) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotAcceptable() } }.andReturn()

    }

    @Test
    fun shouldStartTrainingForUser() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val savedUser = userRepository.save(userGenerator.getUser(id = 0L))
        val savedTrainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser.id,
                trainerId = savedUser.reporter!!.id
            )
        )
        val startTrainingRequest = StartTrainingRequest(
            trainingDetailsId = savedTrainingDetails.id,
            userId = savedUser.id,
            trainerId = savedUser.reporter!!.id,
            domain = Domain.BACKEND,
            completionTime = 45
        )

        val result = mockMvc.put("/admin/training/start") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(startTrainingRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, Response::class.java)

        Assertions.assertEquals(response.response, "Training started")
    }

    @Test
    fun getAllTrainingDetailsForAdmin() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val savedUser1 = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))
        val trainingDetails1 = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser1.id,
                trainerId = savedUser1.reporter!!.id
            )
        )

        val savedUser2 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole))
         val trainingDetails2 = trainingDetailsRepository.save(
            TrainingDetails(
                userId = savedUser2.id,
                trainerId = savedUser2.reporter!!.id
            )
        )

        val trainingDetailsRequestFilter = TrainingDetailsRequestFilter()
        val trainingDetailsList = listOf(trainingDetails1, trainingDetails2)

        val result = mockMvc.get("/admin/training") {
            param("pageNumber", "0")
            param("pageSize", "10")
            content = ObjectMapper().writeValueAsString(trainingDetailsRequestFilter)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<TrainingDetailsResponse>>() {}
        )!!

        Assertions.assertEquals(response.contents.size, trainingDetailsList.size)
        for(i in response.contents.indices){
            Assertions.assertEquals(response.contents[i].trainingDetailsId, trainingDetailsList[i].id)
            Assertions.assertEquals(response.contents[i].trainerId, trainingDetailsList[i].trainerId)
            Assertions.assertEquals(response.contents[i].userId, trainingDetailsList[i].userId)
        }
    }

    @Test
    fun getAllTraineesForTrainer() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val trainer = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))
        val token = "Bearer ${JwtService.generateToken(trainer.authUser)}"

        val trainee1 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole, reporter = trainer))
        val trainee2 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test3", authRole = employeeRole, reporter = trainer))

        val traineesList = listOf(
            trainingDetailsRepository.save(
                TrainingDetails(
                    userId = trainee1.id,
                    trainerId = trainee1.reporter!!.id
                )
            ),
            trainingDetailsRepository.save(
                TrainingDetails(
                    userId = trainee2.id,
                    trainerId = trainee2.reporter!!.id
                )
            )
        )

        val result = mockMvc.get("/employee/training/trainees") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<List<TrainingDetailsResponse>>() {}
        )!!

        Assertions.assertEquals(response.size, traineesList.size)
        for (i in response.indices) {
            Assertions.assertEquals(response[i].trainingDetailsId, traineesList[i].id)
            Assertions.assertEquals(response[i].trainerId, traineesList[i].trainerId)
            Assertions.assertEquals(response[i].userId, traineesList[i].userId)
        }
    }
}