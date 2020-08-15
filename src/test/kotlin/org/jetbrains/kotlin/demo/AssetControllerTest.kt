package org.jetbrains.kotlin.demo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.NestedServletException
import java.net.URL
import java.time.Duration

@AutoConfigureMockMvc
@WebMvcTest(AssetController::class)
class AssetControllerTest {

    @TestConfiguration
    class AssetControllerTestConfig {
        @Bean
        fun s3Service() = mockk<S3Service>()

        @Bean
        fun assetStatusRepository() = mockk<AssetStatusRepository>(relaxed = true)
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var s3Service: S3Service

    @Autowired
    lateinit var assetStatusRepository: AssetStatusRepository

    @Test
    fun testPostAsset() {
        every { s3Service.generatePresignedUrlForPut(any(), any(), any(), any()) } returns URL("http://s3url.com")
        mockMvc.perform(MockMvcRequestBuilders.post("/assets"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.presignedUrl").value("http://s3url.com"))
                .andExpect(jsonPath("$.assetId").isString)
                .andReturn()
        verify { assetStatusRepository.set(any(), eq("uploading")) }
    }

    @Test
    fun testPutAssetStatus_success() {
        mockMvc.perform(MockMvcRequestBuilders.put("/assets/1234/status")
                .content("uploaded"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
        verify { assetStatusRepository.set(eq("1234"), eq("uploaded")) }
    }

    @Test
    fun testPutAssetStatus_failure() {
        every { assetStatusRepository.set(any(), any()) } throws Exception()
        mockMvc.perform(MockMvcRequestBuilders.put("/assets/1234/status")
                .content("uploaded"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(false))
                .andReturn()
    }

    @Test
    fun testGetAsset_defaultTimeout() {
        every { assetStatusRepository.get(eq("1234")) } returns "uploaded"
        every { s3Service.generatePresignedUrlForGet(any(), any(), eq(Duration.ofMinutes(1))) } returns URL("http://s3url.com")
        mockMvc.perform(MockMvcRequestBuilders.get("/assets/1234"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.presignedUrl").value("http://s3url.com"))
                .andReturn()
    }

    @Test
    fun testGetAsset_withTimeout() {
        every { assetStatusRepository.get(eq("1234")) } returns "uploaded"
        every { s3Service.generatePresignedUrlForGet(any(), any(), eq(Duration.ofMinutes(2))) } returns URL("http://s3url.com")
        mockMvc.perform(MockMvcRequestBuilders.get("/assets/1234")
                .param("timeout", "120"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.presignedUrl").value("http://s3url.com"))
                .andReturn()
    }

    @Test
    fun testGetAsset_unknownAsset() {
        every { assetStatusRepository.get(eq("1234")) } returns "uploading"
        every { s3Service.generatePresignedUrlForGet(any(), any(), eq(Duration.ofMinutes(1))) } returns URL("http://s3url.com")
        assertThrows<NestedServletException> { mockMvc.perform(MockMvcRequestBuilders.get("/assets/1234")) }
    }
}
