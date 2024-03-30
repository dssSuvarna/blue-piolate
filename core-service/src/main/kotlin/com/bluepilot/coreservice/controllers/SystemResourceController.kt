package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.requests.CreateSystemResourcesRequest
import com.bluepilot.coreservice.models.requests.UpdateSystemResourceRequest
import com.bluepilot.coreservice.models.responses.SystemResourcesResponse
import com.bluepilot.coreservice.services.SystemResourceService
import com.bluepilot.models.responses.Response
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/resource")
@PreAuthorize("hasAnyRole('HR','ADMIN')")
@Validated
class SystemResourceController @Autowired constructor(
    private val systemResourceService: SystemResourceService
)  {

    @PostMapping
    @PreAuthorize("hasPermission('hasAccess','system.resources.update')")
    fun addSystemResource(@Valid @RequestBody systemResourcesRequest: CreateSystemResourcesRequest) : ResponseEntity<SystemResourcesResponse> {
        return ResponseEntity.ok().body(systemResourceService.addSystemResources(systemResourcesRequest))
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission('hasAccess','system.resources.update')")
    fun updateSystemResource(@Valid @RequestBody request: UpdateSystemResourceRequest) : ResponseEntity<SystemResourcesResponse> {
        return ResponseEntity.ok().body(systemResourceService.updateSystemResource(request))
    }

    @DeleteMapping("/{systemId}")
    @PreAuthorize("hasPermission('hasAccess','system.resources.update')")
    fun deleteSystemResourceBySystemId(@PathVariable systemId: String): ResponseEntity<Response>{
        systemResourceService.deleteSystemResourceBySystemId(systemId)
        return ResponseEntity.ok().body(Response("Resource deleted"))
    }

    @GetMapping("/{systemResourceId}")
    @PreAuthorize("hasPermission('hasAccess','system.resources.view')")
    fun findSystemResourceBySystemId(@PathVariable systemResourceId: Long): ResponseEntity<SystemResourcesResponse>{
        return ResponseEntity.ok().body(systemResourceService.fetchSystemResourceBySystemId(systemResourceId))
    }

    @GetMapping
    @PreAuthorize("hasPermission('hasAccess','system.resources.view')")
    fun getAllSystemResources(): ResponseEntity<List<SystemResourcesResponse>> {
        return ResponseEntity.ok().body(systemResourceService.fetchAllSystemResources())
    }
}