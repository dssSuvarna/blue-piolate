package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.BaseTestConfig
import com.bluepilot.coreservice.generators.HolidayListRequestGenerator
import com.bluepilot.coreservice.services.HolidayService
import com.bluepilot.entities.HolidayList
import com.bluepilot.enums.Role
import com.bluepilot.repositories.AuthRoleRepository
import com.bluepilot.repositories.UserRepository
import com.bluepilot.test.generators.UserGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class HolidayControllerTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val holidayService: HolidayService,
    private val roleRepository: AuthRoleRepository,
    private val userGenerator: UserGenerator
) : BaseTestConfig() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldAddOrUpdateHolidayList() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"

        val holidayListRequest =
            HolidayListRequestGenerator.generateHolidayListRequest(month1 = "Jan", month2 = "Feb")
        val result1 = mockMvc.post("/admin/holidays") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(holidayListRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result1.response.contentAsString, HolidayList::class.java)
        assertEquals(holidayListRequest.year, response.year)
        assertEquals(holidayListRequest.holidays[0].month, response.holidays[0].month)

        //Updating the already saved holiday list
        val updatedMonth1 = "May"
        val updatedMonth2 = "Jun"
        val updateHolidayListRequest = HolidayListRequestGenerator.generateHolidayListRequest(
            id = response.id,
            month1 = updatedMonth1,
            month2 = updatedMonth2
        )
        val result2 = mockMvc.post("/admin/holidays") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(updateHolidayListRequest)
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val updatedResponse = ObjectMapper()
            .readValue(result2.response.contentAsString, HolidayList::class.java)
        assertEquals(holidayListRequest.year, response.year)
        assertEquals(holidayListRequest.holidays[0].month, response.holidays[0].month)

        for (i in 0 until response.holidays.size) {
            assertEquals(updateHolidayListRequest.holidays[i].month, updatedResponse.holidays[i].month)
        }
    }

    @Test
    fun shouldGetHolidayListByYear() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val holidayListRequest =
            HolidayListRequestGenerator.generateHolidayListRequest(month1 = "Jan", month2 = "Feb")
        val holidayList = holidayService.saveOrUpdateHolidayList(holidayListRequest)

        val result = mockMvc.get("/admin/holidays/${holidayList.year}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, HolidayList::class.java)

        assertEquals(holidayList.year, response.year)
    }

    @Test
    fun shouldGetAllHolidaysForAYearForEmployee() {
        val employeeRole = roleRepository.getAuthRoleByName(Role.EMPLOYEE)!!
        val employee = userGenerator.getUser(userName = "username9", authRole = employeeRole)
        val token = "Bearer ${JwtService.generateToken(employee.authUser)}"

        val holidayListRequest =
            HolidayListRequestGenerator.generateHolidayListRequest(month1 = "Jan", month2 = "Feb")
        val holidayList = holidayService.saveOrUpdateHolidayList(holidayListRequest)

        val result = mockMvc.get("/employee/holidays/${holidayList.year}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper()
            .readValue(result.response.contentAsString, HolidayList::class.java)

        assertEquals(holidayList.year, response.year)
    }

    @Test
    fun shouldGetAllHolidayList() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val holidayListRequest1 =
            HolidayListRequestGenerator.generateHolidayListRequest(year = 2021, month1 = "Jan", month2 = "Feb")
        val holidayListRequest2 =
            HolidayListRequestGenerator.generateHolidayListRequest(year = 2022, month1 = "March", month2 = "April")
        val holidayListRequest3 =
            HolidayListRequestGenerator.generateHolidayListRequest(year = 2023, month1 = "Jun", month2 = "Jul")
        val holidayListRequest4 =
            HolidayListRequestGenerator.generateHolidayListRequest(year = 2024, month1 = "Nov", month2 = "Dec")

        val savedHolidayLists = mutableListOf<HolidayList>()
        listOf(holidayListRequest1, holidayListRequest2, holidayListRequest3, holidayListRequest4).forEach {
            savedHolidayLists.add(holidayService.saveOrUpdateHolidayList(it))
        }

        val result = mockMvc.get("/admin/holidays") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }.andReturn()

        val response = ObjectMapper().readValue(
            result.response.contentAsString, object : TypeReference<List<HolidayList>>() {}
        )!!

        assertEquals(savedHolidayLists.size, response.size)
        for (i in response.indices) {
            assertEquals(savedHolidayLists[i].year, response[i].year)
            for (j in response[i].holidays.indices) {
                assertEquals(savedHolidayLists[i].holidays[j].month, response[i].holidays[j].month)
            }
        }
    }

    @Test
    fun shouldThrowNotFoundException() {
        val adminUser = userRepository.findById(1)
        val token = "Bearer ${JwtService.generateToken(adminUser.get().authUser)}"
        val invalidYear = 2000
        val holidayListRequest =
            HolidayListRequestGenerator.generateHolidayListRequest(year = 2021, month1 = "Jan", month2 = "Feb")
        holidayService.saveOrUpdateHolidayList(holidayListRequest)

        val result = mockMvc.get("/admin/holidays/${invalidYear}") {
            contentType = MediaType.APPLICATION_JSON
            headers { header(name = "Authorization", token) }
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotFound() } }.andReturn()

        val errorResponse = ObjectMapper().readTree(result.response.contentAsString)
        val errorMessage: String? = errorResponse["message"].asText()

        assertEquals(errorMessage, "Holiday List for $invalidYear not found")
    }
}