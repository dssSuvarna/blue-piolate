package com.bluepilot.authservice.controller

import com.bluepilot.authservice.models.requests.AuthRequest
import com.bluepilot.authservice.models.requests.GenerateOtpRequest
import com.bluepilot.authservice.models.requests.ResetPasswordRequest
import com.bluepilot.authservice.models.requests.VerificationRequest
import com.bluepilot.authservice.models.responses.LoginResponse
import com.bluepilot.authservice.models.responses.OtpVerificationResponse
import com.bluepilot.authservice.models.responses.UserRolesPermissionsResponse
import com.bluepilot.authservice.services.AuthService
import com.bluepilot.authservice.services.OtpService
import com.bluepilot.configs.JwtService
import com.bluepilot.errors.InvalidCredentials
import com.bluepilot.errors.InvalidOtp
import com.bluepilot.errors.InvalidToken
import com.bluepilot.exceptions.BadCredentialsException
import com.bluepilot.exceptions.InvalidOtpException
import com.bluepilot.exceptions.InvalidTokenException
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping
@Validated
class AuthController @Autowired constructor(
    val authService: AuthService,
    val otpService: OtpService
) {

    @PostMapping("/login")
    fun getToken(@Valid @RequestBody authRequest: AuthRequest): ResponseEntity<LoginResponse> {
        try {
            authService.isAuthenticated(authRequest)
            return ResponseEntity.accepted().body(
                LoginResponse(
                    authService.generateToken(authRequest.username)
                )
            )
        } catch (e: Exception) {
            throw BadCredentialsException(InvalidCredentials())
        }
    }

    @GetMapping("/validate")
    fun validateToken(@RequestParam("token") token: String): ResponseEntity<Response> {
        return try {
            authService.validateToken(token)
            ResponseEntity.accepted().body(Response("Valid token"))
        } catch (e: Exception) {
            ResponseEntity.accepted().body(Response("Invalid token"))
        }
    }

    @GetMapping("/role")
    fun getUserRolePermissionsFromToken(
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<UserRolesPermissionsResponse> {
        return try {
            ResponseEntity.ok().body(authService.getUserRolesPermissionFromToken(token.substring(7)))
        } catch (e: Exception) {
            throw InvalidTokenException(InvalidToken())
        }
    }

    @PostMapping("/generate-otp")
    fun generateOtp(
        @Valid @RequestBody generateOtpRequest: GenerateOtpRequest,
    ): ResponseEntity<Response> {
        otpService.generateAndStoreOtp(generateOtpRequest.username)
        return ResponseEntity.accepted().body(
            Response(
                response = "Otp successfully generated and sent to your email",
            )
        )
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(@RequestBody verificationRequest: VerificationRequest): ResponseEntity<OtpVerificationResponse> {
        val username = verificationRequest.username
        val userEnteredOtp = verificationRequest.otp
        return if (otpService.isOtpValid(username, userEnteredOtp)) {
            val authUser = authService.findByUsername(username)
            val token = JwtService.generateToken(authUser, expiryTime = 600000L )
            ResponseEntity.accepted().body(
                OtpVerificationResponse(
                    response = "Valid otp",
                    token = token
                )
            )
        } else {
            throw InvalidOtpException(InvalidOtp())
        }
    }

    @PutMapping("/reset-password")
    @PreAuthorize("hasPermission('hasAccess','user.update.password')")
    fun resetPassword(
        @Valid @RequestBody resetPasswordRequest: ResetPasswordRequest,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        authService.resetPassword(resetPasswordRequest, token.substring(7))
        return ResponseEntity.accepted().body(Response("Password Reset Successfully"))
    }
}
