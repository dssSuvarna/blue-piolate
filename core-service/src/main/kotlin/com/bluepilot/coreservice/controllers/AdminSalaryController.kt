package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.mappers.SalaryDetailsMapper
import com.bluepilot.coreservice.models.requests.AddSalaryDetailsRequest
import com.bluepilot.coreservice.models.requests.EmployeeSalaryUpdateRequest
import com.bluepilot.coreservice.models.requests.SalaryRequestFilter
import com.bluepilot.coreservice.models.requests.UpdateSalaryDetailsRequest
import com.bluepilot.coreservice.models.responses.EmployeeSalaryResponse
import com.bluepilot.coreservice.models.responses.SalaryDetailsResponse
import com.bluepilot.coreservice.services.SalaryService
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/salary")
@Validated
class AdminSalaryController @Autowired constructor(
    salaryService: SalaryService,
    val salaryDetailsMapper: SalaryDetailsMapper
) : AbstractSalaryController(salaryService) {

    @PostMapping("/details")
    @PreAuthorize("hasPermission('hasAccess','user.update.salary.details')")
    fun addSalaryDetails(
        @Valid @RequestBody addSalaryDetailsRequest: AddSalaryDetailsRequest
    ): ResponseEntity<SalaryDetailsResponse> {
        return ResponseEntity.ok().body(salaryService.addSalaryDetails(addSalaryDetailsRequest))
    }

    @PutMapping("/details")
    @PreAuthorize("hasPermission('hasAccess','user.update.salary.details')")
    fun addNewSalaryDetails(
        @Valid @RequestBody updateSalaryDetailsRequest: UpdateSalaryDetailsRequest
    ): ResponseEntity<SalaryDetailsResponse> {
        return ResponseEntity.ok().body(
            salaryDetailsMapper.toResponse(
                salaryService.addNewSalaryDetails(updateSalaryDetailsRequest)
            )
        )
    }

    @PutMapping("/increment")
    @PreAuthorize("hasPermission('hasAccess','user.update.salary.details')")
    fun incrementSalary(
        @Valid @RequestBody addSalaryDetailsRequest: AddSalaryDetailsRequest
    ): ResponseEntity<SalaryDetailsResponse> {
        return ResponseEntity.ok().body(salaryService.incrementSalary(addSalaryDetailsRequest))
    }

    @PostMapping("/employee-salary")
    @PreAuthorize("hasPermission('hasAccess','user.employee.salary.details.view')")
    fun getAllEmployeesSalaryDetails(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestBody salaryRequestFilter: SalaryRequestFilter
    ): ResponseEntity<PageResponse<EmployeeSalaryResponse>> {
        return ResponseEntity.ok()
            .body(salaryService.getAllEmployeeSalaryWithFilter(pageNumber, pageSize, salaryRequestFilter))
    }

    @PostMapping("/employee-salary/generate")
    @PreAuthorize("hasPermission('hasAccess','user.employee.salary.generate')")
    fun generateEmployeesSalary(): ResponseEntity<Response> {
        salaryService.generateEmployeesSalary()
        return ResponseEntity.ok().body(Response(response = "Salary Generated"))
    }

    @PutMapping("/employee-salary/update")
    @PreAuthorize("hasPermission('hasAccess','user.employee.salary.update')")
    fun updateEmployeesSalary(
        @RequestBody employeeSalaryUpdateRequest: EmployeeSalaryUpdateRequest
    ): ResponseEntity<Response> {
        salaryService.updateEmployeesSalary(employeeSalaryUpdateRequest)
        return ResponseEntity.ok().body(Response(response = "Salary updated"))
    }

    @PutMapping("/employee-salary/status/update/{employeeSalaryId}")
    @PreAuthorize("hasPermission('hasAccess','user.employee.salary.update')")
    fun verifyEmployeeSalary(
        @RequestParam status: EmployeeSalaryStatus,
        @PathVariable employeeSalaryId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        salaryService.updateEmployeeSalaryStatus(status, employeeSalaryId, token)
        return ResponseEntity.ok().body(Response(response = "Status Updated"))
    }
}