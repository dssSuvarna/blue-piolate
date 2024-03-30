package com.bluepilot.entities

import com.bluepilot.enums.NotificationEventType
import com.bluepilot.enums.NotificationStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.sql.Date
import java.util.UUID


@Entity
@Table(name = "notification", schema = DataBaseUtils.SCHEMA.NOTIFICATION_SERVICE)
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val uuid: UUID,
    @Enumerated(EnumType.STRING)
    val notificationEventType: NotificationEventType,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    val payload: Map<String, String?>,
    @Enumerated(EnumType.STRING)
    var status: NotificationStatus,
    val reason: String? = null,
    @CreationTimestamp
    val createdAt: Date = Date(Instant.now().toEpochMilli()),
    @UpdateTimestamp
    var updatedAt: Date = Date(Instant.now().toEpochMilli()),
)

