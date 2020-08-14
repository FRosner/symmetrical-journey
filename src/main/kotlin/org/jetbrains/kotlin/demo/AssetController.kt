package org.jetbrains.kotlin.demo

import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@RestController
class AssetController(private val s3Service: S3Service) {

    private val bucketName = "2b6eb14f-7d72-4d77-8262-a8b5bae27324"

    private val assetStatuses = ConcurrentHashMap<String, String>()

    @PostMapping("/assets")
    fun postAsset(): PostAssetResponse {
        val assetId = UUID.randomUUID().toString()
        val presignedUrl = s3Service.generatePresignedUrlForPut(
                bucketName = bucketName,
                keyName = assetId,
                contentType = "text/plain",
                signatureDuration = Duration.ofMinutes(2)
        )
        assetStatuses.put(assetId, "uploading")
        return PostAssetResponse(presignedUrl, assetId)
    }

    @PutMapping("/assets/{assetId}/status")
    fun putAssetStatus(
            @PathVariable("assetId") assetId: String,
            @RequestBody status: String
    ): PutAssetStatusResponse {
        try {
            assetStatuses.put(assetId, status)
        } catch (e: Exception) {
            return PutAssetStatusResponse(false)
        }
        return PutAssetStatusResponse(true)
    }

    @GetMapping("/assets/{assetId}")
    fun getAsset(
            @PathVariable("assetId") assetId: String,
            @RequestParam(value = "timeout", required = false, defaultValue = "60") timeout: Long
    ): GetAssetResponse {
        if (assetStatuses.getOrDefault(assetId, "default") != "uploaded") {
            throw IllegalStateException("asset $assetId not in state 'uploaded'")
        }
        val signatureDuration = Duration.ofSeconds(timeout)
        val presignedUrl = s3Service.generatePresignedUrlForGet(bucketName, assetId, signatureDuration)
        return GetAssetResponse(presignedUrl)
    }

}

