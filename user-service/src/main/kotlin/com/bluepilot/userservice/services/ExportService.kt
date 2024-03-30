package com.bluepilot.userservice.services

import com.bluepilot.entities.User
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Month
import com.bluepilot.errors.ErrorMessages.Companion.EMPLOYEE_SALARY_DETAILS_NOT_FOUND
import com.bluepilot.errors.ErrorMessages.Companion.ESI_PF_DETAILS_NOT_FOUND
import com.bluepilot.errors.NotFoundError
import com.bluepilot.errors.ResourceNotFound
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.EmployeeSalaryRepository
import com.bluepilot.repositories.EsiAndPFDetailsRepository
import com.bluepilot.userservice.mappers.EmployeeSalarySpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ExportService @Autowired constructor(
    val sheetExporter: SheetExporter,
    val esiAndPFDetailsRepository: EsiAndPFDetailsRepository,
    val employeeSalaryRepository: EmployeeSalaryRepository,
    val pdfExporter: PdfExporter
) {
    fun exportESISheetOfAllEmployee(): Resource {
        val esiAndPFDetails = esiAndPFDetailsRepository.findAll()
        return sheetExporter.exportESISheet(esiAndPFDetails)
    }

    fun exportSheetByESIAndPFId(esiPfDetailsId: Long): Resource {
        val esiAndPFDetails = esiAndPFDetailsRepository
            .findById(esiPfDetailsId).orElseThrow { ResourceNotFound(message = ESI_PF_DETAILS_NOT_FOUND) }
        return sheetExporter.exportESISheet(listOf(esiAndPFDetails))
    }

    fun exportCMSSheet(month: Month, year: Int): Resource {
        val spec = EmployeeSalarySpecification.withFilter(month, year, EmployeeSalaryStatus.VERIFIED)
        val employeeSalaryDetails = employeeSalaryRepository.findAll(spec, Pageable.unpaged()).content
        return sheetExporter.exportCMSSheet(employeeSalaryDetails, month, year)
    }

    fun exportPayslipForUser(user: User, month: Month, year: Int): ByteArray {
        val employeeSalaryDetail = employeeSalaryRepository.findByUserIdAndMonthAndYear(user.id, month, year)
        if(employeeSalaryDetail == null || employeeSalaryDetail.status != EmployeeSalaryStatus.PAID)
            throw NotFoundException(NotFoundError(message = EMPLOYEE_SALARY_DETAILS_NOT_FOUND))
        return pdfExporter.payslipExporter(employeeSalaryDetail, user)
    }
}