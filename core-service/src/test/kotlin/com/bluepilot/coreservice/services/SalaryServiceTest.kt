package com.bluepilot.coreservice.services

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.OnboardedContextGenerator
import com.bluepilot.coreservice.models.requests.AddSalaryDetailsRequest
import com.bluepilot.coreservice.models.requests.UpdateSalaryDetailsRequest
import com.bluepilot.entities.OnboardingContext
import com.bluepilot.entities.SalaryDetails
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.OnboardingContextStatus
import com.bluepilot.enums.UserStatus
import com.bluepilot.errors.ErrorMessages.Companion.NOT_ALLOWED_TO_SAVE_SALARY_DETAILS
import com.bluepilot.errors.ErrorMessages.Companion.SALARY_DETAILS_NOT_PRESENT
import com.bluepilot.exceptions.NotAllowedException
import com.bluepilot.repositories.EmployeeSalaryRepository
import com.bluepilot.repositories.OnboardingContextRepository
import com.bluepilot.repositories.RoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.RoundingMode

@SpringBootTest
class SalaryServiceTest @Autowired constructor(
    val onboardingContextRepository: OnboardingContextRepository,
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val roleRepository: RoleRepository,
    val salaryService: SalaryService,
    val employeeSalaryRepository: EmployeeSalaryRepository
) : BaseTestConfig() {

    lateinit var onboardingContext: OnboardingContext

    @BeforeEach
    fun saveOnboardingContext() {
        onboardingContext = onboardingContextRepository.save(OnboardedContextGenerator.getOnBoardedContext(
            onboardingContextStatus = OnboardingContextStatus.APPROVED
        ))
    }

    @Test
    fun shouldThrowNotAllowedExceptionToSaveSalaryDetailsIfAlreadyExists() {
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
        val savedUser = userRepository.save(user)

        val exception: Throwable = Assertions.assertThrows(NotAllowedException::class.java) {
            //Throws NotAllowedException as salary details is already added
            salaryService.addSalaryDetails(
                AddSalaryDetailsRequest(
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
            )
        }.error

        Assertions.assertEquals(
            NOT_ALLOWED_TO_SAVE_SALARY_DETAILS.replace("{user}", savedUser.firstName), exception.message
        )
    }

    @Test
    fun shouldThrowNotAllowedExceptionIfUpdatingEmptySalaryDetails() {
        val employeeRole = roleRepository.findById(1).get()
        val savedUser = userRepository.save(userGenerator.getUser(authRole = employeeRole))

        val exception: Throwable = Assertions.assertThrows(NotAllowedException::class.java) {
            //Throws NotAllowedException as salary details is not added and if update action is performed
            salaryService.addNewSalaryDetails(
                UpdateSalaryDetailsRequest(
                    userId = savedUser.id,
                    pt = BigDecimal(100.00),
                    it = BigDecimal(100.00),
                    pf = BigDecimal(100.00),
                    esi = BigDecimal(100.00),
                )
            )
        }.error

        Assertions.assertEquals(
            SALARY_DETAILS_NOT_PRESENT.replace("{user}", savedUser.firstName), exception.message
        )
    }

    @Test
    fun adminShouldVerifyTheEmployeeSalary() {
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
        salaryService.generateEmployeesSalary()
        val empSalary = employeeSalaryRepository.findAll().first()
        salaryService.updateEmployeeSalaryStatus(EmployeeSalaryStatus.VERIFIED,empSalary.id,token)
        val updatedSalary = employeeSalaryRepository.findAll().first()
        Assertions.assertEquals(updatedSalary.status, EmployeeSalaryStatus.VERIFIED)
    }

    @Test
    fun hrShouldNotVerifyTheEmployeeSalary() {
        val hr = userRepository.findById(2L).get()
        val employeeRole = roleRepository.findById(1).get()
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole, status = UserStatus.ACTIVE))
        val token = "Bearer ${JwtService.generateToken(hr.authUser)}"
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
        salaryService.generateEmployeesSalary()
        val empSalary = employeeSalaryRepository.findAll().first()
        Assertions.assertThrows(NotAllowedException::class.java, Executable {
            salaryService.updateEmployeeSalaryStatus(EmployeeSalaryStatus.VERIFIED,empSalary.id,token)
        })
    }
}