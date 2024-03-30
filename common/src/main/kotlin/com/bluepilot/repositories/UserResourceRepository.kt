package com.bluepilot.repositories

import com.bluepilot.entities.UserResource
import org.springframework.data.jpa.repository.JpaRepository

interface UserResourceRepository: JpaRepository<UserResource, Long>{
    fun findByUserId(userId: Long): UserResource?
}