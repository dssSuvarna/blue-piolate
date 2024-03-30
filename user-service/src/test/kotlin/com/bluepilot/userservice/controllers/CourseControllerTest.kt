package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.TrainingDetails
import com.bluepilot.enums.ProgressStatus
import com.bluepilot.enums.Role
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.repositories.UserCourseRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.generators.CreateCourseRequestGenerator
import com.bluepilot.userservice.models.responses.CourseResponse
import com.bluepilot.userservice.models.responses.UserCourseResponse
import com.bluepilot.userservice.services.CourseService
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
import org.springframework.test.web.servlet.post

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class CourseControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val courseService: CourseService,
    val trainingDetailsRepository: TrainingDetailsRepository,
    val userCourseRepository: UserCourseRepository,
    val authRoleRepository: AuthRoleRepository
): BaseTestConfig()  {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun saveCourseForAdmin() {
        val courseRequest = CreateCourseRequestGenerator.generateCourse()
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val result = mockMvc.post("/admin/course") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(courseRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, CourseResponse::class.java)

        Assertions.assertEquals(response.name, courseRequest.name)
        Assertions.assertEquals(response.hours, courseRequest.contents.sumOf { it.hours })
        Assertions.assertEquals(response.contents!!.size, courseRequest.contents.size)
        for (i in response.contents!!.indices){
            Assertions.assertEquals(response.contents!![i].name, courseRequest.contents[i].name)
            Assertions.assertEquals(response.contents!![i].description, courseRequest.contents[i].description)
            Assertions.assertEquals(response.contents!![i].hours, courseRequest.contents[i].hours)
            Assertions.assertEquals(response.contents!![i].type, courseRequest.contents[i].type)
        }
    }

    @Test
    fun getAllCoursesByUser() {
        val user = userRepository.save(userGenerator.getUser())
        val courseRequests = listOf(
            CreateCourseRequestGenerator.generateCourse(courseName = "course 1"),
            CreateCourseRequestGenerator.generateCourse(courseName = "course 2"),
            CreateCourseRequestGenerator.generateCourse(courseName = "course 3")
        )
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        courseRequests.forEach {
            courseService.saveCourse(it, token)
        }

        val result = mockMvc.get("/employee/course") {
            param("pageNumber", 0.toString())
            param("pageSize", 10.toString())
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val courseResponse = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<PageResponse<CourseResponse>>() {}
        )!!
        Assertions.assertEquals(courseResponse.totalCount,3)
        Assertions.assertEquals(courseResponse.contents[0].name,"course 1")
        Assertions.assertEquals(courseResponse.contents[1].name,"course 2")
        Assertions.assertEquals(courseResponse.contents[2].name,"course 3")
    }

    @Test
    fun assignCourseToUserByAdmin() {
        val user = userRepository.save(userGenerator.getUser())
        val courseRequest = CreateCourseRequestGenerator.generateCourse(courseName = "course 1")
        val trainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = user.id,
                trainerId = user.reporter!!.id
            )
        )

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val savedCourse = courseService.saveCourse(courseRequest, token)

        mockMvc.post("/admin/course/assign") {
            contentType = MediaType.APPLICATION_JSON
            param("courseId", "${savedCourse.courseId}")
            param("trainingDetailsId", "${trainingDetails.id}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val userCourse = userCourseRepository.findAll().first()
        Assertions.assertEquals(userCourse.course.id, savedCourse.courseId)
        Assertions.assertEquals(userCourse.status, ProgressStatus.NOT_STARTED)
        Assertions.assertEquals(userCourse.trainingDetails.userId,user.id)
    }

    @Test
    fun assignCourseToUserByUser() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val courseRequest = CreateCourseRequestGenerator.generateCourse(courseName = "course 1")
        val trainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = user.id,
                trainerId = user.reporter!!.id
            )
        )

        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val savedCourse = courseService.saveCourse(courseRequest, token)

        mockMvc.post("/employee/course/assign") {
            contentType = MediaType.APPLICATION_JSON
            param("courseId", "${savedCourse.courseId}")
            param("trainingDetailsId", "${trainingDetails.id}")
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val userCourse = userCourseRepository.findAll().first()
        Assertions.assertEquals(userCourse.course.id, savedCourse.courseId)
        Assertions.assertEquals(userCourse.status, ProgressStatus.NOT_STARTED)
        Assertions.assertEquals(userCourse.trainingDetails.userId, user.id)
    }

    @Test
    fun canNotStartInProgressCourse() {
        val user = userRepository.save(
            userGenerator.getUser(authRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)!!)
        )
        val courseRequest = CreateCourseRequestGenerator.generateCourse(courseName = "course 1")
        val trainingDetails = trainingDetailsRepository.save(
            TrainingDetails(
                userId = user.id,
                trainerId = user.reporter!!.id
            )
        )
        val adminUser = userRepository.findById(1)
        val adminToken = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val savedCourse = courseService.saveCourse(courseRequest, adminToken)
        // Assign course to user
        courseService.assignCourseToUser(savedCourse.courseId, trainingDetails.id, adminToken)
        val assignedCourse = userCourseRepository.findAll().first()
        assignedCourse.status = ProgressStatus.IN_PROGRESS
        userCourseRepository.save(assignedCourse)
        val userToken = "Bearer ${JwtService.generateToken(user.authUser)}"

        mockMvc.post("/employee/course/start/${assignedCourse.id}") {
            headers { header(name = "Authorization", userToken) }
        }.andExpect { status { isNotAcceptable() } }.andReturn()
    }

    @Test
    fun getCoursesAssignedForUserByHisTrainee() {
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val trainer = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))
        val token = "Bearer ${JwtService.generateToken(trainer.authUser)}"

        val trainee = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole, reporter = trainer))
        val trainingDetailsOfTrainee = trainingDetailsRepository.save(
            TrainingDetails(
                userId = trainee.id,
                trainerId = trainee.reporter!!.id
            )
        )

        val courseRequest = CreateCourseRequestGenerator.generateCourse(courseName = "course 1")
        val savedCourse = courseService.saveCourse(courseRequest, token)
        courseService.assignCourseToUser(savedCourse.courseId, trainingDetailsOfTrainee.id, token)

        val result = mockMvc.get("/employee/course/user-courses/${trainingDetailsOfTrainee.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val userCourseResponse = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<List<UserCourseResponse>>() {}
        )!!.first()

        Assertions.assertEquals(userCourseResponse.course.courseId, savedCourse.courseId)
        Assertions.assertEquals(userCourseResponse.course.name, savedCourse.name)
        Assertions.assertEquals(userCourseResponse.status, ProgressStatus.NOT_STARTED)
    }

    @Test
    fun getCourseByCourseId() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val courseRequest = CreateCourseRequestGenerator.generateCourse()
        val savedCourse = courseService.saveCourse(courseRequest, token)

        val result = mockMvc.get("/admin/course/${savedCourse.courseId}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, CourseResponse::class.java)

        Assertions.assertEquals(response.name, savedCourse.name)
        Assertions.assertEquals(response.hours, savedCourse.hours)
        Assertions.assertEquals(response.contents!!.size, savedCourse.contents!!.size)
        for (i in response.contents!!.indices){
            Assertions.assertEquals(response.contents!![i].name, savedCourse.contents!![i].name)
            Assertions.assertEquals(response.contents!![i].description, savedCourse.contents!![i].description)
            Assertions.assertEquals(response.contents!![i].hours, savedCourse.contents!![i].hours)
            Assertions.assertEquals(response.contents!![i].type, savedCourse.contents!![i].type)
        }
    }
}