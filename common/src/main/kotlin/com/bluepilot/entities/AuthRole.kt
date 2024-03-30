package com.bluepilot.entities

import com.bluepilot.enums.Role
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "auth_role", schema = DataBaseUtils.SCHEMA.AUTH_SERVICE)
data class AuthRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    val name: Role
) {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "auth_role_permission",
        schema = DataBaseUtils.SCHEMA.AUTH_SERVICE,
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id", referencedColumnName = "id")]
    )
    var permissions: MutableSet<AuthPermission> = mutableSetOf()
}
