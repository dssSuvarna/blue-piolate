package com.bluepilot.repositories

import com.bluepilot.entities.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUuid(uuid: UUID): Notification?
}