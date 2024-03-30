package com.bluepilot.repositories

import com.bluepilot.entities.SystemResources
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SystemResourcesRepository: JpaRepository<SystemResources, Long>{

    fun findBySystemId(systemId: String): SystemResources?
}