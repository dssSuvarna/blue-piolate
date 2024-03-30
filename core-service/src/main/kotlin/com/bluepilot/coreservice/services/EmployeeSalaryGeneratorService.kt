package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.models.requests.EmployeeSalaryUpdateRequest
import com.bluepilot.coreservice.models.responses.EmployeeSalaryContext
import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.entities.SalaryDetails
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class EmployeeSalaryGeneratorService {
    private var basic: BigDecimal = BigDecimal(0.0)
    private var hra: BigDecimal = BigDecimal(0.0)
    private var specialAllowances: BigDecimal = BigDecimal(0.0)
    private var performanceIncentive: BigDecimal = BigDecimal(0.0)
    private var pt: BigDecimal = BigDecimal(0.0)
    private var it: BigDecimal = BigDecimal(0.0)
    private var pf: BigDecimal = BigDecimal(0.0)
    private var esi: BigDecimal = BigDecimal(0.0)
    private var ytdBasic: BigDecimal = BigDecimal(0.0)
    private var ytdHra: BigDecimal = BigDecimal(0.0)
    private var ytdSpecialAllowances: BigDecimal = BigDecimal(0.0)
    private var ytdBonus: BigDecimal = BigDecimal(0.0)
    private var ytdPt: BigDecimal = BigDecimal(0.0)
    private var ytdPf: BigDecimal = BigDecimal(0.0)
    private var ytdEsi: BigDecimal = BigDecimal(0.0)
    private var ytdIt: BigDecimal = BigDecimal(0.0)
    private var ytdOtherDeductions: BigDecimal = BigDecimal(0.0)
    private var advance: BigDecimal = BigDecimal(0.0)
    private var bonus: BigDecimal = BigDecimal(0.0)
    private var ytdPmEarnings: BigDecimal = BigDecimal(0.0)
    private var ytdPmBasic: BigDecimal = BigDecimal(0.0)
    private var ytdPmHra: BigDecimal = BigDecimal(0.0)
    private var ytdPmSpecialAllowances: BigDecimal = BigDecimal(0.0)
    private var ytdPmBonus: BigDecimal = BigDecimal(0.0)
    private var ytdPmPf: BigDecimal = BigDecimal(0.0)
    private var ytdPmEsi: BigDecimal = BigDecimal(0.0)
    private var ytdPmPt: BigDecimal = BigDecimal(0.0)
    private var ytdPmIt: BigDecimal = BigDecimal(0.0)
    private var ytdPmOtherDeductions: BigDecimal = BigDecimal(0.0)
    private var ytdPmDeductions: BigDecimal = BigDecimal(0.0)

    fun generateSalaryContext(
        salaryDetails: SalaryDetails,
        preMonthEmployeeSalary: EmployeeSalary?,
        daysInMonth: BigDecimal,
        totalPayableDays: BigDecimal
    ): EmployeeSalaryContext {
        initializeClassProperty(
            daysInMonth,
            totalPayableDays,
            salaryDetails,
            preMonthEmployeeSalary = preMonthEmployeeSalary
        )
        return getEmployeeContext(daysInMonth, totalPayableDays)
    }

    fun initializeClassProperty(
        daysInMonth: BigDecimal,
        totalPayableDays: BigDecimal,
        salaryDetails: SalaryDetails,
        preMonthEmployeeSalary: EmployeeSalary? = null,
        basic: BigDecimal? = null,
        hra: BigDecimal? = null,
        specialAllowances: BigDecimal? = null,
        pt: BigDecimal? = null,
        it: BigDecimal? = null,
        pf: BigDecimal? = null,
        esi: BigDecimal? = null,
        performanceIncentive: BigDecimal? = null,
        bonus: BigDecimal? = null,
        advance: BigDecimal? = null
    ) {
        this.basic = getAmount(daysInMonth, totalPayableDays, basic ?: salaryDetails.basic)
        this.hra = getAmount(daysInMonth, totalPayableDays, hra ?: salaryDetails.hra)
        this.specialAllowances =
            getAmount(daysInMonth, totalPayableDays, specialAllowances ?: salaryDetails.specialAllowances)
        this.performanceIncentive = performanceIncentive ?: salaryDetails.performanceIncentive
        this.pt = getAmount(daysInMonth, totalPayableDays, pt ?: salaryDetails.pt)
        this.it = getAmount(daysInMonth, totalPayableDays, it ?: salaryDetails.it)
        this.pf = getAmount(daysInMonth, totalPayableDays, pf ?: salaryDetails.pf)
        this.esi = getAmount(daysInMonth, totalPayableDays, esi ?: salaryDetails.esi)
        ytdBasic = this.basic + (preMonthEmployeeSalary?.ytdBasic ?: BigDecimal(0.0))
        ytdHra = this.hra + (preMonthEmployeeSalary?.ytdHra ?: BigDecimal(0.0))
        ytdSpecialAllowances = this.specialAllowances + (preMonthEmployeeSalary?.ytdSpecialAllowances ?: BigDecimal(0.0))
        ytdBonus = (bonus ?: BigDecimal(0.0)) + (preMonthEmployeeSalary?.ytdBonus ?: BigDecimal(0.0))
        ytdPt = this.pt + (preMonthEmployeeSalary?.ytdPt ?: BigDecimal(0.0))
        ytdPf = this.pf + (preMonthEmployeeSalary?.ytdPf ?: BigDecimal(0.0))
        ytdEsi = this.esi + (preMonthEmployeeSalary?.ytdEsi ?: BigDecimal(0.0))
        ytdIt = this.it + (preMonthEmployeeSalary?.ytdIt ?: BigDecimal(0.0))
        ytdOtherDeductions = BigDecimal(0.0) + (preMonthEmployeeSalary?.ytdOtherDeductions ?: BigDecimal(0.0))
        this.advance = advance ?: BigDecimal(0.0)
        this.bonus = bonus ?: BigDecimal(0.0)
        ytdPmEarnings = preMonthEmployeeSalary?.ytdEarnings ?: BigDecimal(0.0)
        ytdPmBasic = preMonthEmployeeSalary?.ytdBasic ?: BigDecimal(0.0)
        ytdPmHra = preMonthEmployeeSalary?.ytdHra ?: BigDecimal(0.0)
        ytdPmSpecialAllowances = preMonthEmployeeSalary?.ytdSpecialAllowances ?: BigDecimal(0.0)
        ytdPmBonus = preMonthEmployeeSalary?.ytdBonus ?: BigDecimal(0.0)
        ytdPmPf = preMonthEmployeeSalary?.ytdPf ?: BigDecimal(0.0)
        ytdPmEsi = preMonthEmployeeSalary?.ytdEsi ?: BigDecimal(0.0)
        ytdPmPt = preMonthEmployeeSalary?.ytdPt ?: BigDecimal(0.0)
        ytdPmIt = preMonthEmployeeSalary?.ytdIt ?: BigDecimal(0.0)
        ytdPmOtherDeductions = preMonthEmployeeSalary?.ytdOtherDeductions ?: BigDecimal(0.0)
        ytdPmDeductions = preMonthEmployeeSalary?.ytdDeductions ?: BigDecimal(0.0)
    }

    fun getUpdatedEmployeeContext(
        daysInMonth: BigDecimal,
        totalPayableDays: BigDecimal,
        salaryDetails: SalaryDetails,
        employeeSalaryUpdateRequest: EmployeeSalaryUpdateRequest,
        preMonthEmployeeSalary: EmployeeSalary? = null
    ): EmployeeSalaryContext {
        initializeClassProperty(
            preMonthEmployeeSalary = preMonthEmployeeSalary,
            totalPayableDays = totalPayableDays,
            daysInMonth = daysInMonth,
            salaryDetails = salaryDetails,
            basic = employeeSalaryUpdateRequest.basic,
            hra = employeeSalaryUpdateRequest.hra,
            specialAllowances = employeeSalaryUpdateRequest.specialAllowances,
            pt = employeeSalaryUpdateRequest.pt,
            it = employeeSalaryUpdateRequest.it,
            pf = employeeSalaryUpdateRequest.pf,
            esi = employeeSalaryUpdateRequest.esi,
            performanceIncentive = employeeSalaryUpdateRequest.performanceIncentive,
            bonus = employeeSalaryUpdateRequest.bonus,
            advance = employeeSalaryUpdateRequest.advance
        )
        return getEmployeeContext(daysInMonth, totalPayableDays)
    }

    private fun getEmployeeContext(daysInMonth: BigDecimal, totalPayableDays: BigDecimal) = EmployeeSalaryContext(
        totalWorkingDays = daysInMonth,
        totalPayableDays = totalPayableDays,
        basic = basic,
        hra = hra,
        specialAllowances = specialAllowances,
        performanceIncentive = performanceIncentive,
        oneTimeIncentive = bonus,
        pt = pt,
        it = it,
        pf = pf,
        esi = esi,
        advance = advance,
        grossEarning = getGrossEarnings(this.bonus),
        grossDeductions = getGrossDeductions(this.advance),
        grossPay = getGrossPay(),
        ytdBasic = ytdBasic,
        ytdHra = ytdHra,
        ytdSpecialAllowances = ytdSpecialAllowances,
        ytdBonus = ytdBonus,
        ytdEarnings = getYtdEarnings(),
        ytdPt = ytdPt,
        ytdPf = ytdPf,
        ytdEsi = ytdEsi,
        ytdIt = ytdIt,
        ytdOtherDeductions = ytdOtherDeductions,
        ytdDeductions = getYtdDeductions(),
        ytdPmEarnings = ytdPmEarnings,
        ytdPmBasic = ytdPmBasic,
        ytdPmHra = ytdPmHra,
        ytdPmSpecialAllowances = ytdPmSpecialAllowances ,
        ytdPmBonus = ytdPmBonus,
        ytdPmPf = ytdPmPf,
        ytdPmEsi = ytdPmEsi,
        ytdPmPt = ytdPmPt,
        ytdPmIt = ytdPmIt,
        ytdPmOtherDeductions = ytdPmOtherDeductions,
        ytdPmDeductions = ytdPmDeductions
    )

    private fun getGrossEarnings(oneTimeIncentive: BigDecimal = BigDecimal(0.0)): BigDecimal {
        return basic + hra + specialAllowances + performanceIncentive + oneTimeIncentive
    }

    private fun getGrossDeductions(advance: BigDecimal = BigDecimal(0.0)): BigDecimal {
        return pt + it + pf + esi + advance
    }

    private fun getGrossPay() = getGrossEarnings() - getGrossDeductions()

    private fun getAmount(totalMonthDays: BigDecimal, totalPayableDays: BigDecimal, amount: BigDecimal): BigDecimal {
        return ((amount / totalMonthDays) * totalPayableDays)
    }

    private fun getYtdDeductions(): BigDecimal = ytdPt + ytdPf + ytdEsi + ytdIt + ytdOtherDeductions

    private fun getYtdEarnings(): BigDecimal = ytdPt + ytdPf + ytdEsi + ytdIt + ytdOtherDeductions
}