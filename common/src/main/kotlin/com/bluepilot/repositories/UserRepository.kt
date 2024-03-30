package com.bluepilot.repositories

import com.bluepilot.entities.User
import com.bluepilot.enums.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByFirstName(firstName: String): User?

    fun findByEmployeeCode(employeeCode: String): User

    fun findAll(specification: Specification<User>, pageable: Pageable): Page<User>

    fun findUserByAuthUser_Role_Name(role: Role): List<User>

    fun findAllByAuthUser_Role_NameNotIn(roles:List<Role>, pageable: Pageable):Page<User>

    @Query("SELECT u FROM User u WHERE u.authUser.username = ?1")
    fun findUserByAuthUser(authUserName: String): User?
}