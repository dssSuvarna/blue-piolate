package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.responses.EmployeeSalaryResponse
import com.bluepilot.entities.EmployeeSalary
import org.springframework.stereotype.Component

@Component
class EmployeeSalaryDetailsMapper {

    fun toResponse(employeeSalary: EmployeeSalary): EmployeeSalaryResponse {
        return EmployeeSalaryResponse(
            id = employeeSalary.id,
            userId = employeeSalary.userId,
            employeeCode = employeeSalary.employeeCode,
            designation = employeeSalary.designation,
            empCode = employeeSalary.employeeCode,
            doj = employeeSalary.doj,
            basic = employeeSalary.basic,
            hra = employeeSalary.hra,
            specialAllowances = employeeSalary.specialAllowances,
            performanceIncentive = employeeSalary.performanceIncentive,
            pt = employeeSalary.pt,
            it = employeeSalary.it,
            pf = employeeSalary.pf,
            esi = employeeSalary.esi,
            oneTimeIncentive = employeeSalary.oneTimeIncentive,
            advance = employeeSalary.advance,
            grossEarning = employeeSalary.grossEarning,
            grossDeductions = employeeSalary.grossDeductions,
            grossPay = employeeSalary.grossPay,
            ytdBasic = employeeSalary.ytdBasic,
            ytdHra = employeeSalary.ytdHra,
            ytdSpecialAllowances = employeeSalary.ytdSpecialAllowances,
            ytdBonus = employeeSalary.ytdBonus,
            ytdEarnings = employeeSalary.ytdEarnings,
            ytdPt = employeeSalary.ytdPt,
            ytdPf = employeeSalary.ytdPf,
            ytdEsi = employeeSalary.ytdEsi,
            ytdOtherDeductions = employeeSalary.ytdOtherDeductions,
            ytdDeductions = employeeSalary.ytdDeductions,
            panNo = employeeSalary.panNo,
            month = employeeSalary.month,
            year = employeeSalary.year,
            dol = employeeSalary.dol,
            totalWorkingDays = employeeSalary.totalWorkingDays,
            totalPayableDays = employeeSalary.totalPayableDays,
            ytdIt = employeeSalary.ytdIt,
            ytdPmEarnings = employeeSalary.ytdPmEarnings,
            ytdPmBasic = employeeSalary.ytdPmBasic,
            ytdPmHra = employeeSalary.ytdPmHra,
            ytdPmSpecialAllowances = employeeSalary.ytdPmSpecialAllowances,
            ytdPmBonus = employeeSalary.ytdPmBonus,
            ytdPmPf = employeeSalary.ytdPmPf,
            ytdPmEsi = employeeSalary.ytdPmEsi,
            ytdPmPt = employeeSalary.ytdPmPt,
            ytdPmIt = employeeSalary.ytdPmIt,
            ytdPmOtherDeductions = employeeSalary.ytdPmOtherDeductions,
            ytdPmDeductions = employeeSalary.ytdPmDeductions,
            status = employeeSalary.status
        )
    }
}