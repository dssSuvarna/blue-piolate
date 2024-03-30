package com.bluepilot.userservice.mappers

import com.bluepilot.entities.TrainingDetails
import com.bluepilot.enums.Domain
import com.bluepilot.enums.TrainingStatus
import com.bluepilot.userservice.models.requests.TrainingDetailsRequestFilter
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object TrainingDetailsSpecification {
    fun withFilter(trainingDetailsRequestFilter: TrainingDetailsRequestFilter): Specification<TrainingDetails> {
        return Specification { root: Root<TrainingDetails>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
            val predicates: MutableList<Predicate> = ArrayList()
            if (trainingDetailsRequestFilter.status != null)
                predicates.add(
                    criteriaBuilder.equal(
                        root.get<TrainingStatus>("status"),
                        trainingDetailsRequestFilter.status
                    )
                )
            if (trainingDetailsRequestFilter.domain != null)
                predicates.add(criteriaBuilder.equal(root.get<Domain>("domain"), trainingDetailsRequestFilter.domain))
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}