package com.bluepilot.coreservice.mappers
import com.bluepilot.coreservice.models.requests.UserRequestFilter
import com.bluepilot.entities.User
import com.bluepilot.enums.UserStatus
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object UserSpecification {
    fun withFilter(userRequestFilter: UserRequestFilter, onlyEmployees: Boolean = true): Specification<User> {
        return Specification { root: Root<User>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            if (userRequestFilter.status != null) {
                predicates.add(criteriaBuilder.equal(root.get<UserStatus>("status"), userRequestFilter.status))
            }
            if(onlyEmployees) {
                predicates.add(criteriaBuilder.notEqual(root.get<String>("designation"), "Administrator"))
                predicates.add(criteriaBuilder.notEqual(root.get<String>("designation"), "Human Resource"))
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}


