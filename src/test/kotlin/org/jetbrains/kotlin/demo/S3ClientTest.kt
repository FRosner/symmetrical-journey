package org.jetbrains.kotlin.demo

import assertk.assertThat
import assertk.assertions.isNotNull
import com.amazonaws.services.s3.AmazonS3Client
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.*

@Testcontainers
class S3ClientTest {

    companion object {
        private val localstack = LocalStackContainer().withServices(LocalStackContainer.Service.S3)

        private val bucketName = UUID.randomUUID().toString()

        @BeforeAll
        fun setupS3() {
            val s3 = AmazonS3Client.builder()
                    .withCredentials(localstack.defaultCredentialsProvider)
                    .withRegion(localstack.region)
                    .build()
            s3.createBucket(bucketName)
        }
    }

    @Test
    fun presignPut() {
        val s3Service = S3Service() // FIXME here I'd have to pass the presigner but it's SDK 2 but localstack has SDK 1...
        val keyName = UUID.randomUUID().toString()
        val presignedUrl = s3Service.generatePresignedUrlForPut(bucketName, keyName, "text/plain", Duration.ofMinutes(1))

        assertThat(presignedUrl).isNotNull()
        /*
        FIXME this should work if the presigner is setup correctly to point to localstack (see above)

        val connection = presignedUrl.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "text/plain")
        connection.requestMethod = "PUT"
        val out = OutputStreamWriter(connection.outputStream)
        out.write("upload content")
        out.close()

        assertThat(connection.responseCode).isEqualTo(200)
        */
    }

    @Test
    fun presignGet() {
        val s3Service = S3Service() // FIXME here I'd have to pass the presigner but it's SDK 2 but localstack has SDK 1...
        val keyName = UUID.randomUUID().toString()
        val presignedUrl = s3Service.generatePresignedUrlForGet(bucketName, keyName, Duration.ofMinutes(1))

        assertThat(presignedUrl).isNotNull()
        /*
            FIXME actually use URL to download but it only works if presigner is setup correctly to point to localstack (see above)
        */
    }

}
