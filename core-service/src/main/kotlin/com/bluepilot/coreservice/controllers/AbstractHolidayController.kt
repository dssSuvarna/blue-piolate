package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.HolidayService
import com.bluepilot.entities.HolidayList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
abstract class AbstractHolidayController @Autowired constructor(val holidayService: HolidayService) {
    @GetMapping("/{year}")
    @PreAuthorize("hasPermission('hasAccess','holiday.list.view')")
    fun getHolidayListByYear(@PathVariable year: Int): ResponseEntity<HolidayList> {
        return ResponseEntity.ok().body(holidayService.fetchHolidayListByYear(year))
    }
}