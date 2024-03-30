package com.bluepilot.authservice.services

import com.bluepilot.authservice.models.requests.AuthRequest
import com.bluepilot.authservice.models.requests.ResetPasswordRequest
import com.bluepilot.authservice.models.responses.UserRolesPermissionsResponse
import com.bluepilot.configs.JwtService
import com.bluepilot.entities.AuthUser
import com.bluepilot.enums.NotificationEventType
import com.bluepilot.errors.ErrorMessages.Companion.AUTH_USER_NOT_FOUND
import com.bluepilot.errors.MisMatchPassword
import com.bluepilot.errors.UserNotFound
import com.bluepilot.exceptions.InvalidPasswordException
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.models.RolePermission
import com.bluepilot.notifications.EventService
import com.bluepilot.repositories.AuthUserRepository
import com.bluepilot.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService @Autowired constructor(
    val authUserRepository: AuthUserRepository,
    val authenticationManager: AuthenticationManager,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val eventService: EventService
) {

    fun generateToken(username: String): String {
        val user = authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(AUTH_USER_NOT_FOUND)
        return JwtService.generateToken(user)
    }

    fun validateToken(token: String) {
        try {
            val username = JwtService.extractUsername(token)
            authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(AUTH_USER_NOT_FOUND)
            JwtService.validateToken(token)
        } catch (e: Exception) {
            throw Exception("Invalid token")
        }

    }

    fun getAuthentication(authRequest: AuthRequest): Authentication =
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authRequest.username,
                authRequest.password
            )
        )

    fun isAuthenticated(authRequest: AuthRequest): Boolean = getAuthentication(authRequest).isAuthenticated

    fun getUserRolesPermissionFromToken(token: String): UserRolesPermissionsResponse {
        val user = userRepository
            .findUserByAuthUser(JwtService.extractUsername(token)) ?: throw NotFoundException(UserNotFound())
        val authRole = JwtService.extractRole(token)
        return UserRolesPermissionsResponse(
            userId = user.id,
            RolePermission(role = authRole.name, permissions = authRole.permissions.map { it.name })
        )
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest, token: String) {
        if (resetPasswordRequest.newPassword != resetPasswordRequest.confirmPassword) {
            throw InvalidPasswordException(MisMatchPassword())
        }
        val username = JwtService.extractUsername(token)
        val authUser = findByUsername(username)
        authUser.password = passwordEncoder.encode(resetPasswordRequest.newPassword)
        eventService.sendEvent(
            notificationEventType = NotificationEventType.PASSWORD_RESET_SUCCESSFULLY,
            additionalData = mapOf("emailTo" to username)
        )
    }

    fun findByUsername(username: String): AuthUser {
        return authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(AUTH_USER_NOT_FOUND)
    }
}










