package com.bluepilot.coreservice.controllers

import com.bluepilot.configs.JwtService
import com.bluepilot.coreservice.S3TestConfig
import com.bluepilot.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.name

@SpringBootTest
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
class DocumentControllerTest @Autowired constructor(
      val userRepository: UserRepository
) : S3TestConfig() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun uploadDocumentTest() {
        val user = userRepository.findById(1).get()
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        val file = Files.createTempFile("text.txt","")
        val multipartFile = MockMultipartFile("file", file.name, MediaType.MULTIPART_FORM_DATA_VALUE, file.inputStream())

        mockMvc.perform(
            multipart("/employee/document/upload")
                .file(multipartFile)
                .header("Authorization", token))
            .andExpect(status().isOk())
        file.deleteIfExists()
    }

    @Test
    fun deleteDocumentTest() {
        val user = userRepository.findById(1).get()
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        val file = Files.createTempFile("text.txt","")
        val multipartFile = MockMultipartFile("file", file.name, MediaType.MULTIPART_FORM_DATA_VALUE, file.inputStream())

        val result = mockMvc.perform(
            multipart("/employee/document/upload")
                .file(multipartFile)
                .header("Authorization", token))
            .andExpect(
                status().isOk()
            ).andReturn()
        val response = ObjectMapper().readValue(result.response.contentAsString, Map::class.java)
        val fileUrl = response["fileUrl"].toString()
        file.deleteIfExists()


        val result2 = mockMvc.post("/employee/document/delete") {
            param("fileUrl", fileUrl)
            headers { header(name = "Authorization", token) }
        }.andExpect {
            status { isOk()}
        }.andReturn()
        val response2 = ObjectMapper().readValue(result2.response.contentAsString, Map::class.java)
        Assertions.assertEquals(response2["response"], "File deleted")
    }

    @Test
    fun getPreSignedUrlTest() {
        val user = userRepository.findById(1).get()
        val token = "Bearer ${JwtService.generateToken(user.authUser)}"

        val file = Files.createTempFile("text.txt","")
        val multipartFile = MockMultipartFile("file", file.name, MediaType.MULTIPART_FORM_DATA_VALUE, file.inputStream())

        val result = mockMvc.perform(
            multipart("/employee/document/upload")
                .file(multipartFile)
                .header("Authorization", token))
            .andExpect(
                status().isOk()
            ).andReturn()
        val response = ObjectMapper().readValue(result.response.contentAsString, Map::class.java)
        val fileUrl = response["fileUrl"].toString()
        file.deleteIfExists()

        val result2 = mockMvc.get("/employee/document/presigned-url") {
            param("fileUrl", fileUrl)
            headers { header(name = "Authorization", token) }
        }.andExpect {
            status { isOk()}
        }.andReturn()
        val response2 = ObjectMapper().readValue(result2.response.contentAsString, Map::class.java)
        Assertions.assertNotNull(response2["preSignedUrl"])
    }
}