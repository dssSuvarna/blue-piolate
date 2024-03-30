package com.bluepilot.userservice.controllers

import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.bluepilot.userservice.services.TrainingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee/training")
class UserTrainingController @Autowired constructor(
    trainingService: TrainingService
) : AbstractTrainingController(trainingService) {

    @GetMapping("/trainees")
    @PreAuthorize("hasRole('EMPLOYEE') AND hasPermission('hasAccess','user.training.view')")
    fun getListOfTrainees(
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<List<TrainingDetailsResponse>> =
        ResponseEntity.ok().body(trainingService.getTraineesForATrainer(token))
}
