package com.bluepilot.entities

import com.bluepilot.enums.UserStatus
import com.bluepilot.utils.DataBaseUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "user", schema = DataBaseUtils.SCHEMA.USER_SERVICE)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var firstName: String,
    var lastName: String,
    val employeeCode: String,
    val designation: String,
    var profilePicture: String? = null,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "auth_user_id")
    val authUser: AuthUser,
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.CREATED,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_details_id")
    var userDetails: UserDetails?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "resource_id")
    var resource: UserResource? = null,
    @OneToOne
    @JoinColumn(name = "reporter_id")
    var reporter: User?
) {
    fun getUserRole() = this.authUser.role.name
}
