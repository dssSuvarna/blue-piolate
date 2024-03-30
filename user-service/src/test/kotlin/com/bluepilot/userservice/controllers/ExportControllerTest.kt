package com.bluepilot.userservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.entities.ESIAndPFDetails
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Role
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.EmployeeSalaryRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.EmployeeSalaryGenerator
import com.bluepilot.test.generators.UserGenerator
import com.bluepilot.userservice.BaseTestConfig
import com.bluepilot.userservice.generators.UpdateESIAndPFDetailsRequestGenerator
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class ExportControllerTest @Autowired constructor(
    val userRepository: UserRepository,
    val userGenerator: UserGenerator,
    val roleRepository: AuthRoleRepository,
    val employeeSalaryRepository: EmployeeSalaryRepository,
    val authUserRepository: AuthUserRepository
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun downloadFileTest() {
        val esiReq = UpdateESIAndPFDetailsRequestGenerator.getESIAndPFDetailsByHRRequest()
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val esi = ESIAndPFDetails(
            empCode = esiReq.empCode,
            uanNo = esiReq.uanNo,
            pfNoOrPfMemberId = esiReq.pfNoOrPfMemberId,
            esicNo = esiReq.esicNo,
            empDoj = esiReq.empDoj,
            pf = esiReq.pf,
            esi = esiReq.esi,
            pt = esiReq.pt,
            email = esiReq.email,
            nationality = esiReq.nationality,
            bankAccountNo = esiReq.bankAccountNo,
            bankName = esiReq.bankName,
            ifscCode = esiReq.ifscCode,
            salaryCategory = esiReq.salaryCategory,
            grossSalaryOfFullMonth = esiReq.grossSalaryOfFullMonth,
            basic = esiReq.basic,
            hra = esiReq.hra,
            conveyAllow = esiReq.conveyAllow,
            cityCompAllow = esiReq.cityCompAllow,
            medAllow = esiReq.medAllow,
            eduAllow = esiReq.eduAllow,
            transport = esiReq.transport,
            tea = esiReq.tea,
            mobileAllow = esiReq.mobileAllow,
            newsPaper = esiReq.newsPaper,
            hostelAllow = esiReq.hostelAllow,
            washingAllow = esiReq.washingAllow,
            foodAllow = esiReq.foodAllow,
            total = esiReq.total,
            remark = esiReq.remark
        )

        val employee = userGenerator.getUser(userName = "username9", authRole = employeeRole)
        employee.userDetails!!.esiAndPFDetails = esi
        val savedEmp = userRepository.save(employee)
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val result = mockMvc.post("/admin/export/esi/sheet/${savedEmp.userDetails!!.id}") {
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        val inputStream = ByteArrayInputStream(result.response.contentAsByteArray)
        val workbook = WorkbookFactory.create(inputStream)
        val fileOutputStream = FileOutputStream("./TEST_ESI_PF1.xlsx")
        workbook.write(fileOutputStream)
        val sheet = workbook.getSheet("NEW EMP DETAILS")
        val row = sheet.getRow(5)
        Assertions.assertEquals(esiReq.empCode, row.getCell(0).toString())
        Assertions.assertEquals(esiReq.uanNo, row.getCell(1).toString())
        Assertions.assertEquals(esiReq.pfNoOrPfMemberId, row.getCell(2).toString())
        Assertions.assertEquals(esiReq.esicNo, row.getCell(3).toString())

        // Close streams
        inputStream.close()
        fileOutputStream.close()
        File("./TEST_ESI_PF1.xlsx").delete()
    }

    @Test
    fun exportCMSSheetTest() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val employeeSalaryDetails = employeeSalaryRepository
            .save(EmployeeSalaryGenerator.getEmployeeSalary(userId = user.id, status = EmployeeSalaryStatus.VERIFIED))

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val result = mockMvc.post("/admin/export/cms/sheet") {
            param("month", "${employeeSalaryDetails.month}")
            param("year", "${employeeSalaryDetails.year}")
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }
            .andReturn()

        val inputStream = ByteArrayInputStream(result.response.contentAsByteArray)
        val workbook = WorkbookFactory.create(inputStream)
        val fileOutputStream = FileOutputStream("./salary_payments.xlsx")
        workbook.write(fileOutputStream)
        val sheet = workbook.getSheet("${employeeSalaryDetails.month.name} ${employeeSalaryDetails.year}")
        val row = sheet.getRow(1)

        Assertions
            .assertEquals(user.userDetails!!.bankDetails!!.accountNumber.toString(), row.getCell(13).toString())
        Assertions.assertEquals(employeeSalaryDetails.grossPay.setScale(2).toString(), row.getCell(7).toString())
        Assertions.assertEquals(user.userDetails!!.bankDetails!!.accountHolderName, row.getCell(10).toString())

        // Close streams
        inputStream.close()
        fileOutputStream.close()
        File("./salary_payments.xlsx").delete()
    }

    @Test
    fun shouldExportPdf() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val employeeSalaryDetails = employeeSalaryRepository
            .save(EmployeeSalaryGenerator.getEmployeeSalary(userId = user.id, status = EmployeeSalaryStatus.PAID))

        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val result = mockMvc.post("/admin/export/payslip/pdf") {
            param("userId", "${user.id}")
            param("month", "${employeeSalaryDetails.month}")
            param("year", "${employeeSalaryDetails.year}")
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        Assertions.assertEquals(result.response.status, 200)
    }

    @Test
    fun shouldNotExportPayslipForUnauthorizedRole() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val employeeSalaryDetails = employeeSalaryRepository
            .save(EmployeeSalaryGenerator.getEmployeeSalary(userId = user.id))

        val employeeUser = authUserRepository.findById(user.authUser.id).get()
        val token = "Bearer ${JwtService.generateToken(employeeUser)}"
        val result = mockMvc.post("/admin/export/payslip/pdf") {
            param("userId", "${user.id}")
            param("month", "${employeeSalaryDetails.month}")
            param("year", "${employeeSalaryDetails.year}")
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isInternalServerError() } }.andReturn()

        Assertions.assertEquals(result.response.status, 500)
        Assertions.assertEquals(result.resolvedException!!.message, "Access Denied")
    }

    @Test
    fun shouldExportPayslipForUser() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val user = userRepository.save(userGenerator.getUser(authRole = employeeRole))
        val employeeSalaryDetails = employeeSalaryRepository
            .save(EmployeeSalaryGenerator.getEmployeeSalary(userId = user.id, status = EmployeeSalaryStatus.PAID))

        val employeeUser = authUserRepository.findById(user.authUser.id).get()
        val token = "Bearer ${JwtService.generateToken(employeeUser)}"
        val result = mockMvc.post("/user/export/payslip/pdf") {
            param("month", "${employeeSalaryDetails.month}")
            param("year", "${employeeSalaryDetails.year}")
            headers { header(name = "Authorization", token) }
        }.andExpect { status { isOk() } }.andReturn()

        Assertions.assertEquals(result.response.status, 200)
    }
}