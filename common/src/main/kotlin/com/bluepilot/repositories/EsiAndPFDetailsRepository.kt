package com.bluepilot.repositories

import com.bluepilot.entities.ESIAndPFDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EsiAndPFDetailsRepository : JpaRepository<ESIAndPFDetails, Long>