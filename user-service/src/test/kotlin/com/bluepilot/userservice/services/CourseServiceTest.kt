package com.bluepilot.userservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.TrainingDetails
import com.bluepilot.enums.ProgressStatus
import com.bluepilot.enums.Role
import com.bluepilot.errors.ErrorMessages.Companion.NOT_ALLOWED
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.TrainingDetailsRepository
import com.bluepilot.repositories.UserCourseRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.generators.CreateCourseRequestGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
class CourseServiceTest @Autowired constructor(
    val courseService: CourseService,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val trainingDetailsRepository: TrainingDetailsRepository,
    val userCourseRepository: UserCourseRepository,
    val authRoleRepository: AuthRoleRepository
): BaseTestConfig() {

    @Test
    fun shouldGetAllCourses() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        courseService.saveCourse(CreateCourseRequestGenerator.generateCourse("course 1"), token)
        courseService.saveCourse(CreateCourseRequestGenerator.generateCourse("course 2"), token)
        courseService.saveCourse(CreateCourseRequestGenerator.generateCourse("course 3"), token)
        val coursesResponse = courseService.getAllCoursesPaged(0,5).contents
        Assertions.assertEquals(coursesResponse.size,3)
        Assertions.assertEquals(coursesResponse[0].name, "course 1")
        Assertions.assertEquals(coursesResponse[1].name, "course 2")
        Assertions.assertEquals(coursesResponse[2].name, "course 3")
    }

    @Test
    fun shouldStartCourseByUser() {
        val user = userRepository.save(userGenerator.getUser())
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
        courseService.assignCourseToUser(savedCourse.courseId,trainingDetails.id,adminToken)
        val assignedCourse = userCourseRepository.findAll().first()
        val userToken = "Bearer ${JwtService.generateToken(user.authUser)}"
        courseService.startCourse(assignedCourse.id,userToken)
        val updatedCourse = userCourseRepository.findAll().first()
        Assertions.assertEquals(ProgressStatus.IN_PROGRESS , updatedCourse.status)
    }

    @Test
    fun shouldNotStartCourseByOtherUser() {
        val user = userRepository.save(userGenerator.getUser(userName = "user1"))
        val user2 = userRepository.save(userGenerator.getUser(userName = "user2"))
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
        courseService.assignCourseToUser(savedCourse.courseId,trainingDetails.id,adminToken)
        val assignedCourse = userCourseRepository.findAll().first()
        // Other user token
        val userToken = "Bearer ${JwtService.generateToken(user2.authUser)}"
        Assertions.assertThrows(
            NotAllowedException::class.java,
            Executable { courseService.startCourse(assignedCourse.id, userToken) })
    }

    @Test
    fun shouldAssignCourseByUser() {
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
        courseService.assignCourseToUser(savedCourse.courseId, trainingDetails.id, token)

        val userCourse = userCourseRepository.findAll().first()
        Assertions.assertEquals(userCourse.course.id, savedCourse.courseId)
        Assertions.assertEquals(userCourse.status, ProgressStatus.NOT_STARTED)
        Assertions.assertEquals(userCourse.trainingDetails.userId, user.id)
    }

    @Test
    fun shouldNotAllowUnauthorizedUserToViewUserCourse(){
        val employeeRole = authRoleRepository.getAuthRoleByName(Role.EMPLOYEE)
        val user1 = userRepository
            .save(userGenerator.getUser(id = 0L, userName = "test1", authRole = employeeRole!!))
        trainingDetailsRepository.save(
            TrainingDetails(
                userId = user1.id,
                trainerId = user1.reporter!!.id
            )
        )
        val token = "Bearer ${JwtService.generateToken(user1.authUser)}"

        val user2 = userRepository
            .save(userGenerator.getUser(id = 5L, userName = "test2", authRole = employeeRole))
        val trainee2 = trainingDetailsRepository.save(
            TrainingDetails(
                userId = user2.id,
                trainerId = user2.reporter!!.id
            )
        )

        val exception = Assertions.assertThrows(NotAllowedException::class.java) {
            courseService.getUserCoursesOfUser(trainee2.id, token)
        }.error

        Assertions.assertEquals(exception.message, NOT_ALLOWED)
    }
}