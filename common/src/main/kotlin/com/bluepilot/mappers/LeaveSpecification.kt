package com.bluepilot.mappers

import com.bluepilot.entities.Leave
import com.bluepilot.entities.LeaveApprover
import com.bluepilot.entities.User
import com.bluepilot.enums.LeaveStatus
import com.bluepilot.enums.LeaveType
import com.bluepilot.models.requests.LeavesApprovalFilter
import com.bluepilot.models.requests.LeavesFilter
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.sql.Date

object LeaveSpecification {

    fun withFilter(user: User, leavesApprovalFilter: LeavesApprovalFilter): Specification<Leave> {
        return Specification { root: Root<Leave>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            val joinApprover = root.join<Leave, LeaveApprover>("approvalFrom")
            predicates.add(criteriaBuilder.equal(joinApprover.get<User>("user"), user))
            if (leavesApprovalFilter.status != null) {
                predicates.add(criteriaBuilder.equal(root.get<LeaveStatus>("status"), leavesApprovalFilter.status))
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }

    fun withFilter(user: User, leavesFilter: LeavesFilter): Specification<Leave> {
        return Specification { root: Root<Leave>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            predicates.add(criteriaBuilder.equal(root.get<User>("user"), user))
            if (leavesFilter.status != null) {
                predicates.add(criteriaBuilder.equal(root.get<LeaveStatus>("status"), leavesFilter.status))
            }
            if (leavesFilter.leaveType != null) {
                predicates.add(criteriaBuilder.equal(root.get<LeaveType>("leaveType"), leavesFilter.leaveType))
            }
            if (leavesFilter.year != null) {
                predicates.add(
                    criteriaBuilder.between(
                        root.get<Date>("appliedDate"),
                        Date.valueOf("${leavesFilter.year}-01-01"),
                        Date.valueOf("${leavesFilter.year+1}-01-01")
                    )
                )
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}