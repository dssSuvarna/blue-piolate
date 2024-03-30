package com.bluepilot.repositories

import com.bluepilot.entities.AuthRole
import com.bluepilot.enums.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository: JpaRepository<AuthRole, Long>{
    fun findByName(name: Role): AuthRole
}