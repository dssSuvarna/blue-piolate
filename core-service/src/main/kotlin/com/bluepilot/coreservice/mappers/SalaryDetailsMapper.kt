package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.responses.SalaryDetailsResponse
import com.bluepilot.entities.SalaryDetails
import org.springframework.stereotype.Component

@Component
class SalaryDetailsMapper {

    fun toResponse(salaryDetails: SalaryDetails): SalaryDetailsResponse {
        return SalaryDetailsResponse(
            id = salaryDetails.id,
            userId = salaryDetails.userId,
            basic = salaryDetails.basic,
            hra = salaryDetails.hra,
            specialAllowances = salaryDetails.specialAllowances,
            performanceIncentive = salaryDetails.performanceIncentive,
            pt = salaryDetails.pt,
            it = salaryDetails.it,
            pf = salaryDetails.pf,
            esi = salaryDetails.esi,
            annualCtc = salaryDetails.annualCtc
        )
    }
}