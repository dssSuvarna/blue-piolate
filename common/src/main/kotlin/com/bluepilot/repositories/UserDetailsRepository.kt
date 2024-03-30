package com.bluepilot.repositories

import com.bluepilot.entities.UserDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDetailsRepository : JpaRepository<UserDetails, Long>