package com.bluepilot.repositories

import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.enums.Month
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeSalaryRepository : JpaRepository<EmployeeSalary, Long> {

    fun findAll(specification: Specification<EmployeeSalary>, pageable: Pageable): Page<EmployeeSalary>

    fun findByUserIdAndMonthAndYear(userId: Long, month: Month, year: Int): EmployeeSalary?

}