package com.bluepilot.repositories

import com.bluepilot.entities.SalaryDetails
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SalaryDetailsRepository : JpaRepository<SalaryDetails, Long> {
    fun findByUserId(userId: Long, sort: Sort, limit: Int = 1): SalaryDetails
}