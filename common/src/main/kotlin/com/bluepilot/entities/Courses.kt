package com.bluepilot.entities

import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.time.Instant

@Entity
@Table(name = "courses", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class Courses(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    var description: String,
    var hours: Int,
    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    var users: List<UserCourse> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "course")
    var contents: List<Contents>,
    val createdBy: Long,
    var updatedBy: Long,
    @CreationTimestamp
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
    @UpdateTimestamp
    var updatedAt: Timestamp = Timestamp.from(Instant.now())
)
