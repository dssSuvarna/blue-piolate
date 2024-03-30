package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.models.requests.UpdateHolidayListRequest
import com.bluepilot.coreservice.services.HolidayService
import com.bluepilot.entities.HolidayList
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/holidays")
@Validated
class AdminHolidayController @Autowired constructor(
    holidayService: HolidayService
) : AbstractHolidayController(holidayService) {

    @PostMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','holiday.list.update')")
    fun saveOrUpdateHolidays(@Valid @RequestBody updateHolidayListRequest: UpdateHolidayListRequest): ResponseEntity<HolidayList> {
        return ResponseEntity.ok().body(holidayService.saveOrUpdateHolidayList(updateHolidayListRequest))
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR','ADMIN') and hasPermission('hasAccess','holiday.list.view')")
    fun getHolidayListForAllYears(): ResponseEntity<List<HolidayList>> {
        return ResponseEntity.ok().body(holidayService.fetchAllHolidayList())
    }
}