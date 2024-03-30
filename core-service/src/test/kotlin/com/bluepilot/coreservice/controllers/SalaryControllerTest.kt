package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.models.requests.AddSalaryDetailsRequest
import com.bluepilot.coreservice.models.requests.EmployeeSalaryUpdateRequest
import com.bluepilot.coreservice.models.requests.SalaryRequestFilter
import com.bluepilot.coreservice.models.requests.UpdateSalaryDetailsRequest
import com.bluepilot.coreservice.models.responses.EmployeeSalaryResponse
import com.bluepilot.coreservice.models.responses.SalaryDetailsResponse
import com.bluepilot.entities.Leave
import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.LeaveDate
import com.bluepilot.entities.SalaryDetails
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.enums.Month
import com.bluepilot.enums.UserStatus
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.EmployeeSalaryRepository
import com.bluepilot.repositories.LeaveRepository
import com.bluepilot.repositories.RoleRepository
import com.bluepilot.repositories.SalaryDetailsRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.EmployeeSalaryGenerator
import com.bluepilot.test.generators.UserGenerator
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
import org.springframework.test.web.servlet.put
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class SalaryControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val userGenerator: UserGenerator,
    val salaryDetailsRepository: SalaryDetailsRepository,
    val employeeSalaryRepository: EmployeeSalaryRepository,
    val authUserRepository: AuthUserRepository,
    val leaveRepository: LeaveRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldSaveSalaryDetails() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        val savedUser = userRepository.save(user)

        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val addSalaryDetailsRequest = AddSalaryDetailsRequest(
            userId = savedUser.id,
            basic = BigDecimal(100.00),
            hra = BigDecimal(100.00),
            specialAllowances = BigDecimal(100.00),
            performanceIncentive = BigDecimal(100.00),
            pt = BigDecimal(100.00),
            it = BigDecimal(100.00),
            pf = BigDecimal(100.00),
            esi = BigDecimal(100.00),
            annualCtc = BigDecimal(1000.00)
        )

        val result = mockMvc.post("/admin/salary/details") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(addSalaryDetailsRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<SalaryDetailsResponse>() {}
        )!!

        Assertions.assertEquals(addSalaryDetailsRequest.basic, response.basic)
        Assertions.assertEquals(addSalaryDetailsRequest.hra, response.hra)
        Assertions.assertEquals(addSalaryDetailsRequest.pf, response.pf)
    }

    @Test
    fun shouldGenerateEmployeeSalary() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE)
        val savedUser = userRepository.save(user)
        savedUser.userDetails!!.salaryDetails = SalaryDetails(
            userId = savedUser.id,
            basic = BigDecimal(8000).setScale(2, RoundingMode.HALF_UP),
            hra = BigDecimal(3752).setScale(2, RoundingMode.HALF_UP),
            specialAllowances = BigDecimal(5200).setScale(2, RoundingMode.HALF_UP),
            performanceIncentive = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pt = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
            it = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pf = BigDecimal(1600).setScale(2, RoundingMode.HALF_UP),
            esi = BigDecimal(132).setScale(2, RoundingMode.HALF_UP),
            annualCtc = BigDecimal(240000.00).setScale(2, RoundingMode.HALF_UP)
        )
        userRepository.save(user)
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..4) {
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().minusMonths(1).withDayOfMonth(
                        YearMonth.of(
                            ZonedDateTime.now().minusMonths(1).year,
                            ZonedDateTime.now().minusMonths(1).month
                        ).lengthOfMonth() - i + 1
                    ).toInstant().toEpochMilli()
                ))
            )
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().withDayOfMonth(i).toInstant().toEpochMilli()
                ))
            )
        }
        val userApprover = LeaveApprover(user = user.reporter!!, status = LeaveStatus.APPROVED)
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            Leave(
                user = user,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.LOP,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            ),
            Leave(
                user = user,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.LOP,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(userApprover, hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        leaveRepository.saveAll(leavesList).toList()

        mockMvc.post("/admin/salary/employee-salary/generate") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val empSalary = employeeSalaryRepository.findAll().first()
        val salaryDetails = savedUser.userDetails!!.salaryDetails!!
        val days = YearMonth.of(
            ZonedDateTime.now().minusMonths(1).year,
            ZonedDateTime.now().minusMonths(1).month
        ).lengthOfMonth()
        Assertions.assertEquals(
            getAmount(
                salaryDetails.basic,
                days.toBigDecimal(),
                days.toBigDecimal() - "8".toBigDecimal()
            ), empSalary.basic
        )
        Assertions.assertEquals(
            getAmount(
                salaryDetails.hra,
                days.toBigDecimal(),
                days.toBigDecimal() - "8".toBigDecimal()
            ), empSalary.hra
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.specialAllowances, days.toBigDecimal(), days.toBigDecimal() - "8".toBigDecimal()),
            empSalary.specialAllowances
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.performanceIncentive, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.performanceIncentive
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pt, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.pt
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.it, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.it
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pf, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.pf
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.esi, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.esi
        )
    }

    @Test
    fun shouldGenerateEmployeeSalaryForJoiningMonth() {
        val employeeRole = roleRepository.findById(1).get()
        val dateOfJoining = Date.valueOf(LocalDate.now().minusMonths(1))
        val user = userGenerator.getUser(
            authRole = employeeRole, status = UserStatus.ACTIVE, dateOfJoining = dateOfJoining
        )
        val savedUser = userRepository.save(user)
        savedUser.userDetails!!.salaryDetails = SalaryDetails(
            userId = savedUser.id,
            basic = BigDecimal(8000).setScale(2, RoundingMode.HALF_UP),
            hra = BigDecimal(3752).setScale(2, RoundingMode.HALF_UP),
            specialAllowances = BigDecimal(5200).setScale(2, RoundingMode.HALF_UP),
            performanceIncentive = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pt = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
            it = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pf = BigDecimal(1600).setScale(2, RoundingMode.HALF_UP),
            esi = BigDecimal(132).setScale(2, RoundingMode.HALF_UP),
            annualCtc = BigDecimal(240000.00).setScale(2, RoundingMode.HALF_UP)
        )
        userRepository.save(user)
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..4) {
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().minusMonths(1).withDayOfMonth(
                        YearMonth.of(
                            ZonedDateTime.now().minusMonths(1).year,
                            ZonedDateTime.now().minusMonths(1).month
                        ).lengthOfMonth() - i + 1
                    ).toInstant().toEpochMilli()
                ))
            )
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().withDayOfMonth(i).toInstant().toEpochMilli()
                ))
            )
        }
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            Leave(
                user = user,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.LOP,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        leaveRepository.saveAll(leavesList).toList()

        mockMvc.post("/admin/salary/employee-salary/generate") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val empSalary = employeeSalaryRepository.findAll().first()
        val salaryDetails = savedUser.userDetails!!.salaryDetails!!
        val daysofMonth = YearMonth.of(
            ZonedDateTime.now().minusMonths(1).year,
            ZonedDateTime.now().minusMonths(1).month
        ).lengthOfMonth()
        val totalPayableDays =
            daysofMonth.toBigDecimal() - BigDecimal(dateOfJoining.toLocalDate().dayOfMonth) - "4".toBigDecimal() + BigDecimal(
                1
            )
        Assertions.assertEquals(
            getAmount(
                salaryDetails.basic,
                daysofMonth.toBigDecimal(),
                totalPayableDays
            ), empSalary.basic
        )
        Assertions.assertEquals(
            getAmount(
                salaryDetails.hra,
                daysofMonth.toBigDecimal(),
                totalPayableDays
            ), empSalary.hra
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.specialAllowances, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.specialAllowances
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.performanceIncentive, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.performanceIncentive
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pt, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.pt
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.it, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.it
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pf, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.pf
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.esi, daysofMonth.toBigDecimal(), totalPayableDays),
            empSalary.esi
        )
    }

    @Test
    fun shouldUpdateEmployeeSalary() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE)
        val savedUser = userRepository.save(user)
        savedUser.userDetails!!.salaryDetails = SalaryDetails(
            userId = savedUser.id,
            basic = BigDecimal(8000).setScale(2, RoundingMode.HALF_UP),
            hra = BigDecimal(3752).setScale(2, RoundingMode.HALF_UP),
            specialAllowances = BigDecimal(5200).setScale(2, RoundingMode.HALF_UP),
            performanceIncentive = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pt = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
            it = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pf = BigDecimal(1600).setScale(2, RoundingMode.HALF_UP),
            esi = BigDecimal(132).setScale(2, RoundingMode.HALF_UP),
            annualCtc = BigDecimal(24000).setScale(2, RoundingMode.HALF_UP),
        )
        val cc = userRepository.save(user)
        cc.userDetails!!.salaryDetails
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"

        val leaveDates = mutableListOf<LeaveDate>()
        for (i in 1..4) {
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().minusMonths(1).withDayOfMonth(
                        YearMonth.of(
                            ZonedDateTime.now().minusMonths(1).year,
                            ZonedDateTime.now().minusMonths(1).month
                        ).lengthOfMonth() - i + 1
                    ).toInstant().toEpochMilli()
                ))
            )
            leaveDates.add(
                LeaveDate(Date(
                    ZonedDateTime.now().withDayOfMonth(i).toInstant().toEpochMilli()
                ))
            )
        }
        val userApprover = LeaveApprover(user = user.reporter!!, status = LeaveStatus.APPROVED)
        val hrApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("hr@gmail.com")!!,
            status = LeaveStatus.APPROVED
        )
        val adminApprover = LeaveApprover(
            user = userRepository.findUserByAuthUser("admin@gmail.com")!!,
            status = LeaveStatus.TO_BE_APPROVED
        )

        val leavesList = listOf(
            Leave(
                user = user,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.LOP,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            ),
            Leave(
                user = user,
                status = LeaveStatus.APPROVED,
                leaveType = LeaveType.LOP,
                leaveDates = leaveDates,
                approvalFrom = mutableListOf(userApprover, hrApprover, adminApprover),
                appliedDate = Date(Instant.now().toEpochMilli()),
                reason = "reason"
            )
        )
        leaveRepository.saveAll(leavesList).toList()

        mockMvc.post("/admin/salary/employee-salary/generate") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        var empSalary = employeeSalaryRepository.findAll().first()
        val salaryDetails = savedUser.userDetails!!.salaryDetails!!
        val days = YearMonth.of(
            ZonedDateTime.now().minusMonths(1).year,
            ZonedDateTime.now().minusMonths(1).month
        ).lengthOfMonth()

        val employeeSalaryUpdateRequest = EmployeeSalaryUpdateRequest(
            empSalaryId = empSalary.id,
            basic = BigDecimal(16000),
            hra = BigDecimal(5752)
        )
        mockMvc.put("/admin/salary/employee-salary/update") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(employeeSalaryUpdateRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        empSalary = employeeSalaryRepository.findAll().first()
        Assertions.assertEquals(
            getAmount(
                employeeSalaryUpdateRequest.basic!!,
                days.toBigDecimal(),
                BigDecimal(days) - BigDecimal(8.0)
            ).setScale(2), empSalary.basic
        )
        Assertions.assertEquals(
            getAmount(
                employeeSalaryUpdateRequest.hra!!,
                days.toBigDecimal(),
                BigDecimal(days) - BigDecimal(8.0)
            ).setScale(2), empSalary.hra
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.specialAllowances, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.specialAllowances
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.performanceIncentive, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.performanceIncentive
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pt, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.pt
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.it, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.it
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.pf, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.pf
        )
        Assertions.assertEquals(
            getAmount(salaryDetails.esi, days.toBigDecimal(), BigDecimal(days) - BigDecimal(8.0)),
            empSalary.esi
        )
    }

    @Test
    fun shouldUpdateSalaryDetails() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        userRepository.save(user)
        user.userDetails!!.salaryDetails = SalaryDetails(
            userId = user.id,
            basic = BigDecimal(100.00),
            hra = BigDecimal(100.00),
            specialAllowances = BigDecimal(100.00),
            performanceIncentive = BigDecimal(100.00),
            pt = BigDecimal(100.00),
            it = BigDecimal(100.00),
            pf = BigDecimal(100.00),
            esi = BigDecimal(100.00),
            annualCtc = BigDecimal(1000.00)
        )
        userRepository.save(user)

        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val updateSalaryDetailsRequest = UpdateSalaryDetailsRequest(
            userId = user.id,
            pt = BigDecimal(50.00),
            it = BigDecimal(50.00),
            pf = BigDecimal(50.00),
            esi = BigDecimal(50.00),
        )

        val result = mockMvc.put("/admin/salary/details") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateSalaryDetailsRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<SalaryDetailsResponse>() {}
        )!!

        Assertions.assertEquals(updateSalaryDetailsRequest.pf, response.pf)
        Assertions.assertEquals(2L, response.id)
    }


    @Test
    fun shouldGetSalaryDetailsResponseByAdminTest() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        userRepository.save(user)
        val salaryDetails = salaryDetailsRepository.save(
            SalaryDetails(
                userId = user.id,
                basic = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                hra = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                specialAllowances = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                performanceIncentive = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                pt = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                it = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                pf = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                esi = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP),
                annualCtc = BigDecimal(1000.00).setScale(2, RoundingMode.HALF_UP)
            )
        )
        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val result = mockMvc.get("/admin/salary/${salaryDetails.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<SalaryDetailsResponse>() {}
        )!!

        Assertions.assertEquals(salaryDetails.id, response.id)
        Assertions.assertEquals(user.id, response.userId)
        Assertions.assertEquals(salaryDetails.basic, response.basic)
        Assertions.assertEquals(salaryDetails.hra, response.hra)
        Assertions.assertEquals(salaryDetails.pf, response.pf)
        Assertions.assertEquals(salaryDetails.annualCtc, response.annualCtc)
        Assertions.assertEquals(salaryDetails.it, response.it)
        Assertions.assertEquals(salaryDetails.performanceIncentive, response.performanceIncentive)
        Assertions.assertEquals(salaryDetails.specialAllowances, response.specialAllowances)
    }

    @Test
    fun shouldGetSalaryDetailsResponseByEmployeeTest() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        userRepository.save(user)
        val salaryDetails = salaryDetailsRepository.save(
            SalaryDetails(
                userId = user.id,
                basic = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                hra = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                specialAllowances = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                performanceIncentive = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                pt = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                it = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                pf = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                esi = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
                annualCtc = BigDecimal(1000).setScale(2, RoundingMode.HALF_UP)
            )
        )
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"
        val result = mockMvc.get("/admin/salary/${salaryDetails.id}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<SalaryDetailsResponse>() {}
        )!!

        Assertions.assertEquals(salaryDetails.id, response.id)
        Assertions.assertEquals(user.id, response.userId)
        Assertions.assertEquals(salaryDetails.basic, response.basic)
        Assertions.assertEquals(salaryDetails.hra, response.hra)
        Assertions.assertEquals(salaryDetails.pf, response.pf)
        Assertions.assertEquals(salaryDetails.annualCtc, response.annualCtc)
        Assertions.assertEquals(salaryDetails.it, response.it)
        Assertions.assertEquals(salaryDetails.performanceIncentive, response.performanceIncentive)
        Assertions.assertEquals(salaryDetails.specialAllowances, response.specialAllowances)
    }

    @Test
    fun shouldIncrementSalaryForUser() {
        val employeeRole = roleRepository.findById(1).get()
        var user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        user.userDetails!!.salaryDetails = SalaryDetails(
            userId = user.id,
            basic = BigDecimal(100.00),
            hra = BigDecimal(100.00),
            specialAllowances = BigDecimal(100.00),
            performanceIncentive = BigDecimal(100.00),
            pt = BigDecimal(100.00),
            it = BigDecimal(100.00),
            pf = BigDecimal(100.00),
            esi = BigDecimal(100.00),
            annualCtc = BigDecimal(1000.00)
        )
        user = userRepository.save(user)

        val salaryIncrementRequest = AddSalaryDetailsRequest(
            userId = user.id,
            basic = BigDecimal(1000.00),
            hra = BigDecimal(1000.00),
            specialAllowances = BigDecimal(1000.00),
            performanceIncentive = BigDecimal(1000.00),
            pt = BigDecimal(1000.00),
            it = BigDecimal(1000.00),
            pf = BigDecimal(1000.00),
            esi = BigDecimal(1000.00),
            annualCtc = BigDecimal(1000.00)
        )

        val adminUser = userRepository.findById(1L).get()
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        val result = mockMvc.put("/admin/salary/increment") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(salaryIncrementRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<SalaryDetailsResponse>() {}
        )!!

        Assertions.assertEquals(2L, response.id)
        Assertions.assertEquals(user.id, response.userId)
        Assertions.assertEquals(salaryIncrementRequest.basic, response.basic)
        Assertions.assertEquals(salaryIncrementRequest.hra, response.hra)
        Assertions.assertEquals(salaryIncrementRequest.pf, response.pf)
        Assertions.assertEquals(salaryIncrementRequest.annualCtc, response.annualCtc)
        Assertions.assertEquals(salaryIncrementRequest.it, response.it)
        Assertions.assertEquals(salaryIncrementRequest.performanceIncentive, response.performanceIncentive)
        Assertions.assertEquals(salaryIncrementRequest.specialAllowances, response.specialAllowances)
    }

    fun shouldGetEmployeeSalaryResponseTest() {
        val employeeRole = roleRepository.findById(1).get()
        val user = userGenerator.getUser(authRole = employeeRole)
        userRepository.save(user)
        val employeeSalary = employeeSalaryRepository.save(EmployeeSalaryGenerator.getEmployeeSalary(userId = user.id))
        val employeeSalary1 = employeeSalaryRepository.save(
            EmployeeSalaryGenerator.getEmployeeSalary(
                userId = user.id,
                month = Month.MAY
            )
        )
        employeeSalaryRepository.save(
            EmployeeSalaryGenerator.getEmployeeSalary(
                userId = user.id,
                month = Month.JANUARY
            )
        )

        val request1 = SalaryRequestFilter(
            userId = user.id,
            month = Month.APRIL
        )
        val admin = authUserRepository.findByUsername("admin@gmail.com")
        val token = "Bearer ${JwtService.generateToken(admin!!)}"
        var result = mockMvc.post("/admin/salary/employee-salary") {
            param("pageNumber", "0")
            param("pageSize", "10")
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(request1)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        var response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<EmployeeSalaryResponse>>() {}
        )!!

        var employeeSalaryResponse = response.contents.first()
        Assertions.assertEquals(response.totalCount, 1)
        Assertions.assertEquals(employeeSalary.id, employeeSalaryResponse.id)
        Assertions.assertEquals(user.id, employeeSalaryResponse.userId)

        result = mockMvc.post("/admin/salary/employee-salary") {
            param("pageNumber", "0")
            param("pageSize", "10")
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(SalaryRequestFilter())
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()


        response = ObjectMapper().readValue(
            result.response.contentAsString,
            object : TypeReference<PageResponse<EmployeeSalaryResponse>>() {}
        )!!

        employeeSalaryResponse = response.contents[1]
        Assertions.assertEquals(response.totalCount, 3)
        Assertions.assertEquals(employeeSalary1.id, employeeSalaryResponse.id)
        Assertions.assertEquals(user.id, employeeSalaryResponse.userId)
    }

    @Test
    fun shouldUpdateEmployeeSalaryStatus() {
        val adminUser = userRepository.findById(1L).get()
        val employeeRole = roleRepository.findById(1).get()
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE))
        val token = "Bearer ${JwtService.generateToken(adminUser.authUser)}"
        user.userDetails!!.salaryDetails = SalaryDetails(
            userId = user.id,
            basic = BigDecimal(8000).setScale(2, RoundingMode.HALF_UP),
            hra = BigDecimal(3752).setScale(2, RoundingMode.HALF_UP),
            specialAllowances = BigDecimal(5200).setScale(2, RoundingMode.HALF_UP),
            performanceIncentive = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pt = BigDecimal(100).setScale(2, RoundingMode.HALF_UP),
            it = BigDecimal(0).setScale(2, RoundingMode.HALF_UP),
            pf = BigDecimal(1600).setScale(2, RoundingMode.HALF_UP),
            esi = BigDecimal(132).setScale(2, RoundingMode.HALF_UP),
            annualCtc = BigDecimal(240000.00).setScale(2, RoundingMode.HALF_UP)
        )
        userRepository.save(user)
        mockMvc.post("/admin/salary/employee-salary/generate") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val empSalary = employeeSalaryRepository.findAll().first()
        mockMvc.put("/admin/salary/employee-salary/status/update/${empSalary.id}") {
            param("status", EmployeeSalaryStatus.VERIFIED.name)
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }

        val updatedSalary = employeeSalaryRepository.findAll().first()
        Assertions.assertEquals(updatedSalary.status, EmployeeSalaryStatus.VERIFIED)
    }

    fun getAmount(amount: BigDecimal, monthTotalDays: BigDecimal, payableDays: BigDecimal): BigDecimal {
        return ((amount / monthTotalDays) * payableDays)
    }
}