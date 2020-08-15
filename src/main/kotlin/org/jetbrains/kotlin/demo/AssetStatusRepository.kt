package org.jetbrains.kotlin.demo

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class AssetStatusRepository {

    private val assetStatuses = ConcurrentHashMap<String, String>()

    fun set(assetId: String, status: String) {
        assetStatuses.put(assetId, status)
    }

    fun get(assetId: String): String? {
        return assetStatuses.get(assetId)
    }

}
