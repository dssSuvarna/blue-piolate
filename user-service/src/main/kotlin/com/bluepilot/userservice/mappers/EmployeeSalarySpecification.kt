package com.bluepilot.userservice.mappers

import com.bluepilot.entities.EmployeeSalary
import com.bluepilot.enums.EmployeeSalaryStatus
import com.bluepilot.enums.Month
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object EmployeeSalarySpecification {
    fun withFilter(month: Month, year: Int, status: EmployeeSalaryStatus): Specification<EmployeeSalary> {
        return Specification { root: Root<EmployeeSalary>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            predicates.add(criteriaBuilder.equal(root.get<Month>("month"), month))
            predicates.add(criteriaBuilder.equal(root.get<Int>("year"), year))
            predicates.add(criteriaBuilder.equal(root.get<EmployeeSalaryStatus>("status"), status))
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}