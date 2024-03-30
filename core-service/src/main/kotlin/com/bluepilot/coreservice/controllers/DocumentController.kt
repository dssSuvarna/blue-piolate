package com.bluepilot.coreservice.controllers

import com.bluepilot.coreservice.services.S3Service
import com.bluepilot.models.responses.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/employee/document")
class DocumentController @Autowired constructor(
    val s3Service: S3Service
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("fileUrl" to s3Service.uploadFile(file)))
    }

    @PostMapping("/delete")
    @PreAuthorize("hasPermission('hasAccess','document.delete')")
    fun deleteFile(@RequestParam("fileUrl") fileUrl: String): ResponseEntity<Response> {
        s3Service.deleteFile(fileUrl)
        return ResponseEntity.ok(Response("File deleted"))
    }

    @GetMapping("/presigned-url")
    fun getPresignedUrl(@RequestParam("fileUrl") fileUrl: String): ResponseEntity<Map<String, String>> {
        s3Service.getPresignedUrl(fileUrl)
        return ResponseEntity.ok(mapOf("preSignedUrl" to s3Service.getPresignedUrl(fileUrl)!!.toExternalForm()))
    }
}