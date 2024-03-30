package com.bluepilot.userservice.services

import com.bluepilot.entities.Contents
import com.bluepilot.entities.Courses
import com.bluepilot.entities.UserContent
import com.bluepilot.entities.UserCourse
import com.bluepilot.enums.ProgressStatus
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.ErrorMessages.Companion.COURSE_IS_ALREADY_ASSIGNED
import com.bluepilot.errors.ErrorMessages.Companion.COURSE_NOT_FOUND
import com.bluepilot.errors.ErrorMessages.Companion.NOT_ALLOWED_TO_ASSIGN_COURSE
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.exceptions.Validator.Companion.validate
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.repositories.CourseRepository
import com.bluepilot.repositories.UserCourseRepository
import com.bluepilot.userservice.mappers.CourseContentResponseMapper
import com.bluepilot.userservice.mappers.UserCourseContentResponseMapper
import com.bluepilot.userservice.models.requests.CreateCourseRequest
import com.bluepilot.userservice.models.responses.CourseResponse
import com.bluepilot.userservice.models.responses.UserCourseResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant

@Service
@Transactional
class CourseService @Autowired constructor(
    val courseContentResponseMapper: CourseContentResponseMapper,
    val courseRepository: CourseRepository,
    val trainingService: TrainingService,
    val userService: UserService,
    val userCourseRepository: UserCourseRepository,
    val userCourseContentResponseMapper: UserCourseContentResponseMapper
) {
    fun saveCourse(createCourseRequest: CreateCourseRequest, token: String): CourseResponse {
        val user = userService.getUserFromToken(token)
        val hours = createCourseRequest.contents.sumOf { it.hours }
        val savedCourse = courseRepository.save(
            Courses(
                name = createCourseRequest.name,
                description = createCourseRequest.description,
                contents = createCourseRequest.contents.map {
                    Contents(
                        name = it.name,
                        description = it.description,
                        hours = it.hours,
                        type = it.type,
                        links = it.links,
                        files = it.files,
                        createdBy = user.id,
                        updatedBy = user.id
                    )
                },
                hours = hours,
                createdBy = user.id,
                updatedBy = user.id
            )
        )
        return courseContentResponseMapper.mapToCourseResponse(savedCourse)
    }

    fun getAllCoursesPaged(pageNumber: Int, pageSize:Int): PageResponse<CourseResponse> {
        val coursePage = courseRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PageResponse(
            totalCount = coursePage.totalElements,
            pageNumber = coursePage.pageable.pageNumber,
            pageSize = coursePage.size,
            currentPageSize = coursePage.pageable.pageSize,
            contents = coursePage.content.map { courseContentResponseMapper.mapToCourseResponse(it) }
        )
    }

    fun assignCourseToUser(courseId: Long, trainingDetailsId: Long, token: String) {
        val trainingDetails = trainingService.getTrainingDetailsById(trainingDetailsId)
        val courseToAssign = getCourseByCourseId(courseId)
        val user = userService.getUserFromToken(token)
        val isValidUser = (user.id == trainingDetails.userId)
        val isAdminOrHr = userService.userIsAdminOrHR(user)
        validate(
            !((trainingService.isTrainerOfTrainingDetail(user, trainingDetails) || isAdminOrHr) || isValidUser),
            NotAllowedException(NotAllowed(message = NOT_ALLOWED_TO_ASSIGN_COURSE))
        )
        validate(
            trainingDetails.courses.map {it.course}.contains(courseToAssign),
            NotAllowedException(NotAllowed(message = COURSE_IS_ALREADY_ASSIGNED))
        )
        userCourseRepository.save(
            UserCourse(
                trainingDetails = trainingDetails,
                course = courseToAssign,
                userContent = courseToAssign.contents.map {
                    UserContent(
                        content = it,
                        status = ProgressStatus.NOT_STARTED,
                    )
                },
                status = ProgressStatus.NOT_STARTED
            )
        )
    }

    fun getCourseByCourseId(courseId: Long): Courses =
        courseRepository.findById(courseId).orElseThrow { NotFoundException(NotFoundError(message = COURSE_NOT_FOUND)) }

    fun getCourseById(courseId: Long): CourseResponse =
        courseContentResponseMapper.mapToCourseResponse(getCourseByCourseId(courseId))

    fun startCourse(userCourseId: Long, token: String) {
        val userCourse =
            userCourseRepository.findById(userCourseId).orElseThrow { NotFoundException(ResourceNotFound()) }
        val user = userService.getUserFromToken(token)
        validate(
            userCourse.trainingDetails.userId != user.id,
            NotAllowedException(NotAllowed(message = ErrorMessages.NOT_ALLOWED_TO_START_COURSE))
        )
        validate(
            userCourse.status == ProgressStatus.IN_PROGRESS,
            NotAllowedException(NotAllowed(message = ErrorMessages.COURSE_ALL_READY_IN_PROGRESS))
        )
        userCourse.startedAt = Timestamp.from(Instant.now())
        userCourse.status = ProgressStatus.IN_PROGRESS
    }

    fun getUserCoursesOfUser(trainingDetailsId: Long, token: String): List<UserCourseResponse> {
        val trainingDetails = trainingService.getTrainingDetailsById(trainingDetailsId)
        val loggedInUser = userService.getUserFromToken(token)
        val isValidUser = (loggedInUser.id == trainingDetails.userId)
        val isAdminOrHr = userService.userIsAdminOrHR(loggedInUser)
        validate(
            !((trainingService.isTrainerOfTrainingDetail(loggedInUser, trainingDetails)) || isValidUser || isAdminOrHr),
            NotAllowedException(NotAllowed())
        )
        return userCourseContentResponseMapper.mapToUserCourseResponse(trainingDetails.courses)
    }
}