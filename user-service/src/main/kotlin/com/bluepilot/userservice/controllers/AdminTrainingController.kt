package com.bluepilot.userservice.controllers

import com.bluepilot.models.responses.PageResponse
import com.bluepilot.userservice.models.requests.TrainingDetailsRequestFilter
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.bluepilot.userservice.services.TrainingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/training")
class AdminTrainingController @Autowired constructor(
    trainingService: TrainingService
) : AbstractTrainingController(trainingService) {

    @GetMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN') AND hasPermission('hasAccess','user.training.view')")
    fun getAllTrainingDetails(
        @RequestParam pageNumber: Int = 0,
        @RequestParam pageSize: Int = 10,
        @RequestBody trainingDetailsRequestFilter: TrainingDetailsRequestFilter
    ): ResponseEntity<PageResponse<TrainingDetailsResponse>> =
        ResponseEntity.ok().body(
            trainingService.getAllTrainingDetails(pageNumber, pageSize, trainingDetailsRequestFilter)
        )
}
