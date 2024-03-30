package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.mappers.EmployeeSalaryDetailsMapper
import com.bluepilot.coreservice.mappers.SalaryDetailsMapper
import com.bluepilot.coreservice.mappers.SalarySpecification
import com.bluepilot.coreservice.models.DateContext
import com.bluepilot.coreservice.models.requests.AddSalaryDetailsRequest
import com.bluepilot.coreservice.models.requests.EmployeeSalaryUpdateRequest
import com.bluepilot.coreservice.models.requests.SalaryRequestFilter
import com.bluepilot.coreservice.models.requests.UpdateSalaryDetailsRequest
import com.bluepilot.coreservice.models.requests.UserRequestFilter
import com.bluepilot.coreservice.models.responses.EmployeeSalaryResponse
import com.bluepilot.coreservice.models.responses.SalaryDetailsResponse
import com.bluepilot.coreservice.utils.StatusTransition
import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.entities.SalaryDetails
import com.bluepilot.entities.User
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Month
import com.bluepilot.enums.Role
import com.bluepilot.enums.UserStatus
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.ErrorMessages.Companion.NOT_ALLOWED_TO_SAVE_SALARY_DETAILS
import com.bluepilot.errors.ErrorMessages.Companion.SALARY_DETAILS_NOT_PRESENT
import com.bluepilot.errors.NotAllowed
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.repositories.EmployeeSalaryRepository
import com.bluepilot.repositories.SalaryDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.Date
import java.time.YearMonth
import java.time.ZonedDateTime

@Service
@Transactional
class SalaryService @Autowired constructor(
    val salaryDetailsRepository: SalaryDetailsRepository,
    val employeeSalaryRepository: EmployeeSalaryRepository,
    val salaryDetailsMapper: SalaryDetailsMapper,
    val employeeSalaryDetailsMapper: EmployeeSalaryDetailsMapper,
    val userService: UserService,
    val employeeSalaryGeneratorService: EmployeeSalaryGeneratorService,
    val leaveService: LeaveService
) {

    fun getSalaryDetails(salaryDetailsId: Long): SalaryDetailsResponse {
        val salaryDetails = salaryDetailsRepository.findById(salaryDetailsId).orElseThrow {
            throw NotFoundException(NotFoundError(message = SALARY_DETAILS_NOT_PRESENT))
        }
        return salaryDetailsMapper.toResponse(salaryDetails)
    }

    fun addSalaryDetails(addSalaryDetailsRequest: AddSalaryDetailsRequest): SalaryDetailsResponse {
        val user = userService.getUserById(addSalaryDetailsRequest.userId)
        if (user.userDetails!!.salaryDetails != null)
            throw NotAllowedException(
                NotAllowed(message = NOT_ALLOWED_TO_SAVE_SALARY_DETAILS.replace("{user}", user.firstName))
            )
        saveSalaryDetails(user, addSalaryDetailsRequest)
        return salaryDetailsMapper.toResponse(user.userDetails!!.salaryDetails!!)
    }

    private fun saveSalaryDetails(user: User, addSalaryDetailsRequest: AddSalaryDetailsRequest) {
        user.userDetails!!.salaryDetails = SalaryDetails(
            userId = user.id,
            basic = addSalaryDetailsRequest.basic,
            hra = addSalaryDetailsRequest.hra,
            specialAllowances = addSalaryDetailsRequest.specialAllowances,
            performanceIncentive = addSalaryDetailsRequest.performanceIncentive,
            pt = addSalaryDetailsRequest.pt,
            it = addSalaryDetailsRequest.it,
            pf = addSalaryDetailsRequest.pf,
            esi = addSalaryDetailsRequest.esi,
            annualCtc = addSalaryDetailsRequest.annualCtc
        )
        userService.saveUser(user)
    }

    fun incrementSalary(addSalaryDetailsRequest: AddSalaryDetailsRequest): SalaryDetailsResponse {
        val user = userService.getUserById(addSalaryDetailsRequest.userId)
        if (user.userDetails!!.salaryDetails == null)
            throw NotAllowedException(
                NotAllowed(message = SALARY_DETAILS_NOT_PRESENT)
            )
        saveSalaryDetails(user, addSalaryDetailsRequest)
        return salaryDetailsMapper.toResponse(user.userDetails!!.salaryDetails!!)
    }

    fun addNewSalaryDetails(
        updateSalaryDetailsRequest: UpdateSalaryDetailsRequest
    ): SalaryDetails {
        val user = userService.getUserById(updateSalaryDetailsRequest.userId)
        val salaryDetails = user.userDetails!!.salaryDetails
            ?: throw NotAllowedException(NotAllowed(message = SALARY_DETAILS_NOT_PRESENT))
        user.userDetails!!.salaryDetails = SalaryDetails(
            userId = user.id,
            basic = salaryDetails.basic,
            hra = salaryDetails.hra,
            specialAllowances = salaryDetails.specialAllowances,
            performanceIncentive = salaryDetails.performanceIncentive,
            pt = updateSalaryDetailsRequest.pt,
            it = updateSalaryDetailsRequest.it,
            pf = updateSalaryDetailsRequest.pf,
            esi = updateSalaryDetailsRequest.esi,
            annualCtc = salaryDetails.annualCtc,
        )
        return user.userDetails!!.salaryDetails!!
    }

    fun getAllEmployeeSalaryWithFilter(
        pageNumber: Int,
        pageSize: Int,
        salaryRequestFilter: SalaryRequestFilter
    ): PageResponse<EmployeeSalaryResponse> {
        val pageReq = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val spec = SalarySpecification.withFilter(salaryRequestFilter)
        val pageResponse = employeeSalaryRepository.findAll(spec, pageReq)
        return getEmployeeSalaryResponse(pageResponse)
    }

    fun getEmployeeSalaryResponse(pageOfUsers: Page<EmployeeSalary>): PageResponse<EmployeeSalaryResponse> {
        return PageResponse(
            totalCount = pageOfUsers.totalElements,
            pageNumber = pageOfUsers.pageable.pageNumber,
            pageSize = pageOfUsers.size,
            currentPageSize = pageOfUsers.pageable.pageSize,
            contents = pageOfUsers.content.map { employeeSalaryDetailsMapper.toResponse(it) }
        )
    }

    fun generateEmployeesSalary() {
        var pageNumber = 0
        var listOfUsers =
            userService.findAll(
                pageNumber = pageNumber++,
                pageSize = 10,
                UserRequestFilter(UserStatus.ACTIVE)
            )
        while (listOfUsers.currentPageSize > 0L) {
            processSalaryGeneration(listOfUsers.contents)
            listOfUsers = userService.findAll(
                pageNumber = pageNumber++,
                pageSize = 10,
                UserRequestFilter(UserStatus.ACTIVE)
            )
        }
    }

    fun updateEmployeesSalary(employeeSalaryUpdateRequest: EmployeeSalaryUpdateRequest) {
        val empSalary = employeeSalaryRepository.findById(employeeSalaryUpdateRequest.empSalaryId).orElseThrow {
            throw NotFoundException(NotFoundError())
        }
        val dateContext = getDateContext()
        val zonedDateTimeOfPreviousMonth = dateContext.zonedDateTimeOfPreviousMonth.minusMonths(1)
        val user = userService.getUserById(empSalary.userId)
        val lops = leaveService.getLopLeavesOfUser(user)

        val employeePreviousTwoMonthSalary = employeeSalaryRepository.findByUserIdAndMonthAndYear(
            user.id,
            Month.valueOf(zonedDateTimeOfPreviousMonth.month.name),
            zonedDateTimeOfPreviousMonth.year
        )

        val updatedContext = employeeSalaryGeneratorService.getUpdatedEmployeeContext(
            daysInMonth = dateContext.daysInMonth,
            totalPayableDays = getTotalPayableDays(user.userDetails!!.dateOfJoining, lops, dateContext),
            salaryDetails = user.userDetails!!.salaryDetails!!,
            preMonthEmployeeSalary = employeePreviousTwoMonthSalary,
            employeeSalaryUpdateRequest = employeeSalaryUpdateRequest
        )
        with(empSalary) {
            totalPayableDays = updatedContext.totalPayableDays
            basic = updatedContext.basic
            hra = updatedContext.hra
            specialAllowances = updatedContext.specialAllowances
            performanceIncentive = updatedContext.performanceIncentive
            oneTimeIncentive = updatedContext.oneTimeIncentive
            pt = updatedContext.pt
            it = updatedContext.it
            pf = updatedContext.pf
            esi = updatedContext.esi
            advance = updatedContext.advance
            grossEarning = updatedContext.grossEarning
            grossDeductions = updatedContext.grossDeductions
            grossPay = updatedContext.grossPay
            ytdBasic = updatedContext.ytdBasic
            ytdHra = updatedContext.ytdHra
            ytdSpecialAllowances = updatedContext.ytdSpecialAllowances
            ytdBonus = updatedContext.ytdBonus
            ytdEarnings = updatedContext.ytdEarnings
            ytdPt = updatedContext.ytdPt
            ytdPf = updatedContext.ytdPf
            ytdEsi = updatedContext.ytdEsi
            ytdIt = updatedContext.ytdIt
            ytdOtherDeductions = updatedContext.ytdOtherDeductions
            ytdDeductions = updatedContext.ytdDeductions
            status = EmployeeSalaryStatus.UPDATED
        }
    }

    fun processSalaryGeneration(users: List<User>) {
        val dateContext = getDateContext()
        val zonedDateTimeOfPreviousMonth = dateContext.zonedDateTimeOfPreviousMonth.minusMonths(1)

        users.forEach {
            val lops = leaveService.getLopLeavesOfUser(it)
            val totalPayableDays = getTotalPayableDays(it.userDetails!!.dateOfJoining, lops, dateContext)
            val preMonthEmployeeSalary = employeeSalaryRepository.findByUserIdAndMonthAndYear(
                it.id,
                Month.valueOf(zonedDateTimeOfPreviousMonth.month.name),
                zonedDateTimeOfPreviousMonth.year
            )
            val employeeSalaryContext = employeeSalaryGeneratorService.generateSalaryContext(
                it.userDetails!!.salaryDetails!!,
                preMonthEmployeeSalary,
                dateContext.daysInMonth,
                totalPayableDays
            )
            val empSalary =
                employeeSalaryRepository.findByUserIdAndMonthAndYear(it.id, dateContext.previousMonth, dateContext.year)
            if (empSalary == null) {
                employeeSalaryRepository.save(
                    EmployeeSalary(
                        userId = it.id,
                        employeeCode = it.employeeCode,
                        designation = it.designation,
                        panNo = it.userDetails!!.panNumber,
                        month = dateContext.previousMonth,
                        year = dateContext.year,
                        doj = it.userDetails!!.dateOfJoining,
                        dol = null, // Need to be update when off boarding task todo
                        totalWorkingDays = employeeSalaryContext.totalWorkingDays,
                        totalPayableDays = employeeSalaryContext.totalPayableDays,
                        basic = employeeSalaryContext.basic,
                        hra = employeeSalaryContext.hra,
                        specialAllowances = employeeSalaryContext.specialAllowances,
                        performanceIncentive = employeeSalaryContext.performanceIncentive,
                        oneTimeIncentive = employeeSalaryContext.oneTimeIncentive,
                        pt = employeeSalaryContext.pt,
                        it = employeeSalaryContext.it,
                        pf = employeeSalaryContext.pf,
                        esi = employeeSalaryContext.esi,
                        advance = employeeSalaryContext.advance,
                        grossEarning = employeeSalaryContext.grossEarning,
                        grossDeductions = employeeSalaryContext.grossDeductions,
                        grossPay = employeeSalaryContext.grossPay,
                        ytdBasic = employeeSalaryContext.ytdBasic,
                        ytdHra = employeeSalaryContext.ytdHra,
                        ytdSpecialAllowances = employeeSalaryContext.ytdSpecialAllowances,
                        ytdBonus = employeeSalaryContext.ytdBonus,
                        ytdEarnings = employeeSalaryContext.ytdEarnings,
                        ytdPt = employeeSalaryContext.ytdPt,
                        ytdPf = employeeSalaryContext.ytdPf,
                        ytdEsi = employeeSalaryContext.ytdEsi,
                        ytdIt = employeeSalaryContext.ytdIt,
                        ytdOtherDeductions = employeeSalaryContext.ytdOtherDeductions,
                        ytdDeductions = employeeSalaryContext.ytdDeductions,
                        ytdPmEarnings = employeeSalaryContext.ytdPmEarnings,
                        ytdPmBasic = employeeSalaryContext.ytdPmBasic,
                        ytdPmHra = employeeSalaryContext.ytdPmHra,
                        ytdPmSpecialAllowances = employeeSalaryContext.ytdPmSpecialAllowances,
                        ytdPmBonus = employeeSalaryContext.ytdPmBonus,
                        ytdPmPt = employeeSalaryContext.ytdPmPt,
                        ytdPmPf = employeeSalaryContext.ytdPmPf,
                        ytdPmEsi = employeeSalaryContext.ytdPmEsi,
                        ytdPmIt = employeeSalaryContext.ytdPmIt,
                        ytdPmOtherDeductions = employeeSalaryContext.ytdPmOtherDeductions,
                        ytdPmDeductions = employeeSalaryContext.ytdPmDeductions
                    )
                )
            }
        }
    }

    fun getDateContext(): DateContext {
        val zonedDateTime = ZonedDateTime.now().minusMonths(1)
        return DateContext(
            previousMonth = Month.valueOf(zonedDateTime.month.name),
            daysInMonth = YearMonth.of(zonedDateTime.year, zonedDateTime.month).lengthOfMonth().toBigDecimal(),
            year = zonedDateTime.year,
            zonedDateTimeOfPreviousMonth = zonedDateTime
        )
    }

    fun getTotalPayableDays(dateOfJoining: Date, lops: BigDecimal, dateContext: DateContext): BigDecimal {
        val dateOfJoiningMonth = Month.valueOf(dateOfJoining.toLocalDate().month.name)
        val dateOfJoiningYear = dateOfJoining.toLocalDate().year
        if (dateContext.year == dateOfJoiningYear && dateContext.previousMonth == dateOfJoiningMonth) {
            return dateContext.daysInMonth - BigDecimal(dateOfJoining.toLocalDate().dayOfMonth) - lops +
                    BigDecimal.valueOf(1)
        }
        return dateContext.daysInMonth - lops
    }

    fun updateEmployeeSalaryStatus(status: EmployeeSalaryStatus, employeeSalaryId: Long, token: String) {
        val employeeSalary =
            employeeSalaryRepository.findById(employeeSalaryId).orElseThrow { NotFoundException(ResourceNotFound()) }
        val loggedInUser = userService.getUserFromToken(token)
        val currentStatus = employeeSalary.status
        if (StatusTransition.employeeSalaryStatusTransition[currentStatus]!!.contains(status)) {
            when (status) {
                EmployeeSalaryStatus.TO_BE_VERIFIED -> {}
                EmployeeSalaryStatus.UPDATED -> {}
                EmployeeSalaryStatus.VERIFIED -> {
                    if (loggedInUser.authUser.role.name == Role.ADMIN) {
                        employeeSalary.status = status
                    } else {
                        throw NotAllowedException(NotAllowed())
                    }
                }
                EmployeeSalaryStatus.PAID -> { employeeSalary.status = status }
            }
        } else {
            throw NotAllowedException(
                error = NotAllowed(
                    message = "${ErrorMessages.INVALID_TRANSITION} can't move $currentStatus to $status"
                )
            )
        }
    }
}