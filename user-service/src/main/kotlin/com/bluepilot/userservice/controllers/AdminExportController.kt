package com.bluepilot.userservice.controllers

import com.bluepilot.enums.Month
import com.bluepilot.errors.NotFoundError
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.UserRepository
import com.bluepilot.userservice.services.ExportService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/export")
class AdminExportController  @Autowired constructor(
    val exportService: ExportService,
    val userRepository: UserRepository
) {

    @PostMapping("/esi/sheet")
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','user.export.sheet')")
    @ResponseBody
    fun exportESISheet(): ResponseEntity<Resource> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ESI_PF.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(exportService.exportESISheetOfAllEmployee())
    }

    @PostMapping("/esi/sheet/{esiPfDetailsId}")
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','user.export.sheet')")
    @ResponseBody
    fun exportESISheetById(@PathVariable esiPfDetailsId: Long): ResponseEntity<Resource> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ESI_PF.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(exportService.exportSheetByESIAndPFId(esiPfDetailsId))
    }

    @PostMapping("/cms/sheet")
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','user.export.sheet')")
    @ResponseBody
    fun exportCMSSheet(@RequestParam month: String, @RequestParam year: Int): ResponseEntity<Resource> {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cms.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(exportService.exportCMSSheet(Month.valueOf(month), year))
    }

    @PostMapping("/payslip/pdf")
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','user.export.pdf')")
    @ResponseBody
    fun exportPayslipPdf(
        @RequestParam userId: Long,
        @RequestParam month: Month,
        @RequestParam year: Int
    ): ResponseEntity<ByteArray> {
        val user = userRepository.findById(userId).orElseThrow { NotFoundException(NotFoundError()) }
        val filename = "Payslips-${user.employeeCode}-${month}_${year}.pdf"
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${filename}")
            .contentType(MediaType.APPLICATION_PDF)
            .body(exportService.exportPayslipForUser(user, month, year))
    }
}