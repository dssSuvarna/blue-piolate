package com.bluepilot.userservice.controllers

import com.bluepilot.enums.Month
import com.bluepilot.userservice.services.ExportService
import com.bluepilot.userservice.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/export")
class UserExportController @Autowired constructor(
    private val exportService: ExportService,
    private val userService: UserService
) {
    @PostMapping("/payslip/pdf")
    @PreAuthorize("hasRole('EMPLOYEE') and hasPermission('hasAccess','user.export.pdf')")
    fun exportPayslipForUser(
        @RequestParam month: Month,
        @RequestParam year: Int,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<ByteArray> {
        val user = userService.getUserFromToken(token)
        val filename = "Payslips-${user.employeeCode}-${month}_${year}.pdf"
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${filename}")
            .contentType(MediaType.APPLICATION_PDF)
            .body(exportService.exportPayslipForUser(user, month, year))
    }
}