package com.bluepilot.userservice.controllers

import com.bluepilot.models.responses.Response
import com.bluepilot.userservice.models.requests.StartTrainingRequest
import com.bluepilot.userservice.models.requests.UpdateTrainingDetailsRequest
import com.bluepilot.userservice.models.responses.TrainingDetailsResponse
import com.bluepilot.userservice.services.TrainingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AbstractTrainingController @Autowired constructor(
    val trainingService: TrainingService
){

    @PutMapping("/update")
    @PreAuthorize("hasPermission('hasAccess','user.training.update')")
    fun updateTrainingDetails(
        @RequestBody updateTrainingDetailsRequest: UpdateTrainingDetailsRequest,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<TrainingDetailsResponse> {
        return ResponseEntity.ok().body(trainingService.updateTrainingDetails(updateTrainingDetailsRequest,token))
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission('hasAccess','user.training.view')")
    fun getTrainingDetailsByUserId(
        @PathVariable userId: Long,
        @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<TrainingDetailsResponse> {
        return ResponseEntity.ok().body(trainingService.getTrainingDetailsByUserId(userId, token))
    }

    @PutMapping("/start")
    @PreAuthorize("hasPermission('hasAccess','user.training.start')")
    fun startTraining(
        @RequestBody startTrainingRequest: StartTrainingRequest, @RequestHeader(name = "Authorization") token: String
    ): ResponseEntity<Response> {
        trainingService.startTrainingForUser(startTrainingRequest, token)
        return ResponseEntity.ok().body(Response("Training started"))
    }
}