package com.bluepilot.repositories

import com.bluepilot.entities.TrainingDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingDetailsRepository : JpaRepository<TrainingDetails, Long>{
    fun findByUserId(userId: Long): TrainingDetails?

    fun findAllByTrainerId(trainerId: Long): List<TrainingDetails>

    fun findAll(specification: Specification<TrainingDetails>, pageable: Pageable): Page<TrainingDetails>
}