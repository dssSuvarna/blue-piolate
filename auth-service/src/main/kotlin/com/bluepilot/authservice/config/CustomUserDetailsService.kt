package com.bluepilot.authservice.config

import com.bluepilot.authservice.models.CustomUserDetails
import com.bluepilot.errors.ErrorMessages
import com.bluepilot.errors.ErrorMessages.Companion.AUTH_USER_NOT_FOUND
import com.bluepilot.repositories.AuthUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService @Autowired constructor(val authUserRepository: AuthUserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val authUser = authUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(AUTH_USER_NOT_FOUND)
        return CustomUserDetails(authUser)
    }
}
