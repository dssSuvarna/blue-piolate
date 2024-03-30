package com.bluepilot.coreservice.services

import com.bluepilot.coreservice.models.requests.UpdateHolidayListRequest
import com.bluepilot.entities.HolidayList
import com.bluepilot.errors.NotFoundError
import com.bluepilot.exceptions.NotFoundException
import com.bluepilot.repositories.HolidaysRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class HolidayService @Autowired constructor(
    private val holidaysRepository: HolidaysRepository
) {
    fun saveOrUpdateHolidayList(updateHolidayListRequest: UpdateHolidayListRequest): HolidayList {
        val holidayList = holidaysRepository.findById(updateHolidayListRequest.id)
            .getOrNull() ?: HolidayList(year = updateHolidayListRequest.year)
        holidayList.holidays = updateHolidayListRequest.holidays
        return holidaysRepository.save(holidayList)
    }

    fun fetchHolidayListByYear(year: Int): HolidayList {
        return holidaysRepository.findByYear(year)
            ?: throw NotFoundException(NotFoundError(message = "Holiday List for $year not found"))
    }

    fun fetchAllHolidayList(): List<HolidayList> {
        return holidaysRepository.findAll()
    }
}