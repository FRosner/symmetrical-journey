package org.jetbrains.kotlin.demo

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration

@Service
/*
    https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/examples-s3-presign.html
 */
class S3Service(private val s3Presigner: S3Presigner = S3Presigner.create()) {

    fun generatePresignedUrlForPut(bucketName: String, keyName: String, contentType: String, signatureDuration: Duration): URL {
        val putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(contentType)
                .build()
        val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .putObjectRequest(putRequest)
                .build()
        val presignedRequest = s3Presigner.presignPutObject(presignRequest)
        return presignedRequest.url()
    }

    fun generatePresignedUrlForGet(bucketName: String, keyName: String, signatureDuration: Duration): URL {
        val getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build()
        val presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(signatureDuration)
                .getObjectRequest(getRequest)
                .build()
        val presignedRequest = s3Presigner.presignGetObject(presignRequest)
        return presignedRequest.url()
    }
}
