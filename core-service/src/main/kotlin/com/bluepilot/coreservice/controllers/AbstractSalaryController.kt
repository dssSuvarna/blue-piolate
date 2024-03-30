package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.responses.SalaryDetailsResponse
import com.bluepilot.coreservice.services.SalaryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractSalaryController @Autowired constructor(
    val salaryService: SalaryService
) {

    @GetMapping("/{salaryDetailsId}")
    @PreAuthorize("hasPermission('hasAccess','user.salary.details.view')")
    fun getSalaryDetails(
        @PathVariable salaryDetailsId: Long
    ): ResponseEntity<SalaryDetailsResponse> {
        return ResponseEntity.ok().body(salaryService.getSalaryDetails(salaryDetailsId))
    }
}