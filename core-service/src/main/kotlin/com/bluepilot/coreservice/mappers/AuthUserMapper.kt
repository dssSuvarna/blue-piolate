package com.bluepilot.coreservice.mappers

import com.bluepilot.coreservice.models.requests.CreateUserRequest
import com.bluepilot.entities.AuthUser
import com.bluepilot.repositories.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
data class AuthUserMapper @Autowired constructor(
    val passwordEncoder: PasswordEncoder,
    val roleRepository: RoleRepository
) {
    @Value("\${config.default-user-password}")
    val password: String = ""
    fun toEntity(createUserRequest: CreateUserRequest): AuthUser {
        return AuthUser(
            username = createUserRequest.professionalEmail,
            password = passwordEncoder.encode(password),
            role = roleRepository.findByName(createUserRequest.role)
        )
    }
}
