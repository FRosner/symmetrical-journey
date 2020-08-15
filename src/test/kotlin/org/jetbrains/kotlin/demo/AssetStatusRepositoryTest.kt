package org.jetbrains.kotlin.demo

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class AssetStatusRepositoryTest {

    @Test
    fun setAndGet() {
        val repository = AssetStatusRepository()
        repository.set("id", "status")
        assertThat(repository.get("id")).isEqualTo("status")
    }

    @Test
    fun getNull() {
        val repository = AssetStatusRepository()
        assertThat(repository.get("id")).isNull()
    }

}
