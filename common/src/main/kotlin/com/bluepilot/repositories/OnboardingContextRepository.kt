package com.bluepilot.repositories

import com.bluepilot.entities.OnboardingContext
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OnboardingContextRepository : JpaRepository<OnboardingContext, Long>{
    fun findByPersonalEmail(personalEmail: String) : OnboardingContext?
}