package com.bluepilot.coreservice.services

import com.bluepilot.errors.BaseError
import com.bluepilot.exceptions.BaseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.time.Duration
import java.util.UUID

@Service
class S3Service @Autowired constructor(val s3Client: S3Client, val s3Presigner: S3Presigner) {

    @Value("\${aws.s3.bucketname}")
    private lateinit var bucketName: String

    @Value("\${aws.s3.key}")
    private lateinit var key: String

    @Value("\${aws.s3.endpoint}")
    private lateinit var endPoint: String

    @Value("\${aws.s3.region}")
    private lateinit var region: String

    fun uploadFile(file: MultipartFile): String {
        val key = "$key${UUID.randomUUID()}${getFileExtension(file.originalFilename!!)}"
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .contentType(file.contentType)
            .key(key)
            .acl(ObjectCannedACL.PRIVATE)
            .build()

        try {
            val fileBytes = RequestBody.fromBytes(file.bytes)
            s3Client.putObject(putObjectRequest, fileBytes)
        } catch (e: Exception) {
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while uploading the object: ${e.message}"
                )
            )
        }
        return getFileUrl(bucketName, key)
    }

    fun deleteFile(fileUrl: String) {
        try {
            val url = URL(fileUrl)
            val filePath = url.path.trimStart('/').replace("$bucketName/", "")
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build()
            s3Client.deleteObject(deleteObjectRequest)
        } catch (e: NoSuchKeyException) {
            // Handle specific exception when the key does not exist
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The specified key '$key' does not exist in the bucket '$bucketName'."
                )
            )
        } catch (e: Exception) {
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the object: ${e.message}"
                )
            )
        }
    }

    private fun getFileUrl(bucketName: String, key: String): String {
        val request = GetUrlRequest.builder()
            .endpoint(URI(endPoint))
            .bucket(bucketName)
            .key(key)
            .build()
        return s3Client.utilities().getUrl(request).toExternalForm()
    }

    fun getPresignedUrl(fileUrl: String): URL? {
        try {
            val url = URL(fileUrl)
             val bucketName = bucketName
            val key = url.path.trimStart('/').replace("$bucketName/", "")

            val expiration = Duration.ofHours(5L)
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

            val presignedUrl: URL = s3Presigner.presignGetObject { builder ->
                builder.signatureDuration(expiration)
                builder.getObjectRequest(getObjectRequest)
            }.url()

            return presignedUrl
        } catch (e: Exception) {
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while generating the pre-signed URL: ${e.message}"
                )
            )
        }
    }

    fun moveFileToDifferentFolder(fileUrl: String, newFolderPath: String): String {
        try {
            val url = URL(fileUrl)
            val oldKey = url.path.trimStart('/').replace("$bucketName/", "")
            val newKey = "$newFolderPath/${oldKey.substringAfterLast('/')}"

            val copyObjectRequest = CopyObjectRequest.builder()
                .copySource("$bucketName/$oldKey")
                .destinationBucket(bucketName)
                .destinationKey(newKey)
                .acl(ObjectCannedACL.PRIVATE)
                .build()

            s3Client.copyObject(copyObjectRequest)

            // Delete the original object from its old location
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(URLEncoder.encode(oldKey, "UTF-8"))
                .build()
            s3Client.deleteObject(deleteObjectRequest)

            return getFileUrl(bucketName, newKey)
        } catch (e: NoSuchKeyException) {
            // Handle specific exception when the key does not exist
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The specified key '$key' does not exist in the bucket '$bucketName'."
                )
            )
        } catch (e: Exception) {
            throw BaseException(
                BaseError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while moving the object: ${e.message}"
                )
            )
        }
    }

    private fun getFileExtension(filename: String): String {
        val lastDotIndex = filename.lastIndexOf('.')
        return if (lastDotIndex >= 0 && lastDotIndex < filename.length - 1) {
            "."+filename.substring(lastDotIndex + 1)
        } else {
            ""
        }
    }
}