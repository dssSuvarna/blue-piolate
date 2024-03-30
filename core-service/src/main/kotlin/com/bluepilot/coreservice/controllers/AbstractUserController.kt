package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.requests.UpdatePasswordRequest
import com.bluepilot.coreservice.models.requests.UserRequestFilter
import com.bluepilot.coreservice.models.responses.UserResponse
import com.bluepilot.coreservice.services.UserService
import com.bluepilot.models.responses.PageResponse
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
abstract class AbstractUserController @Autowired constructor(val userService: UserService) {

    @PutMapping("/update-password")
    @PreAuthorize("hasPermission('hasAccess','user.update.password')")
    fun updatePassword(
        @Valid @RequestBody updatePasswordRequest: UpdatePasswordRequest,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        userService.updatePassword(updatePasswordRequest, token.substring(7))
        return ResponseEntity.accepted().body(Response( "Password updated"))
    }

    @PostMapping
    @PreAuthorize("hasPermission('hasAccess','user.view')")
    fun getAllUsers(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestBody userRequestFilter: UserRequestFilter
    ): ResponseEntity<PageResponse<UserResponse>> {
        return ResponseEntity.ok().body(userService.getAllUsersWithFilter(pageNumber, pageSize, userRequestFilter))
    }

    @PostMapping("/profile-picture")
    @PreAuthorize("hasPermission('hasAccess','user.update')")
    fun updateProfilePicture(
        @RequestHeader(name = "Authorization") token: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Response> {
        userService.updateProfilePicture(token, file)
        return ResponseEntity.ok().body(Response("Profile picture updated"))
    }
}