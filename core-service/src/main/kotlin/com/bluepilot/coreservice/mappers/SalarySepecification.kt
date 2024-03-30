package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.SalaryRequestFilter
import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.enums.Month
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object SalarySpecification {
    fun withFilter(salaryRequestFilter: SalaryRequestFilter): Specification<EmployeeSalary> {
        return Specification { root: Root<EmployeeSalary>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            if (salaryRequestFilter.month != null) {
                predicates.add(criteriaBuilder.equal(root.get<Month>("month"), salaryRequestFilter.month))
            }
            if (salaryRequestFilter.userId != null) {
                predicates.add(criteriaBuilder.equal(root.get<Long>("userId"), salaryRequestFilter.userId))
            }

            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}


