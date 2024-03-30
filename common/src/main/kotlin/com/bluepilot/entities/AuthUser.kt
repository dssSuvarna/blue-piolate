package com.bluepilot.entities

import com.bluepilot.enums.AuthUserStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "auth_user", schema = DataBaseUtils.SCHEMA.AUTH_SERVICE)
data class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var username: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    var status: AuthUserStatus = AuthUserStatus.ENABLED,
    @OneToOne
    @JoinColumn(name = "auth_role_id")
    var role: AuthRole
)
