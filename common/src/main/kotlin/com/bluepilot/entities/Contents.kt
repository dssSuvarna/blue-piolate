package com.bluepilot.entities

import com.bluepilot.enums.ContentType
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.sql.Timestamp
import java.time.Instant

@Entity
@Table(name = "contents", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class Contents(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    var description: String,
    val hours: Int,
    @Enumerated(EnumType.STRING)
    var type: ContentType,
    var pointsAllotted: Int = 10,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var links: List<String> = mutableListOf(),
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    var files: List<String> = mutableListOf(),
    val createdBy: Long,
    var updatedBy: Long,
    @CreationTimestamp
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
    @UpdateTimestamp
    var updatedAt: Timestamp = Timestamp.from(Instant.now())
)
