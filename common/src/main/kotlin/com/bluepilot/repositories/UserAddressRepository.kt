package com.bluepilot.repositories

import com.bluepilot.entities.UserAddress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository : JpaRepository<UserAddress, Long>