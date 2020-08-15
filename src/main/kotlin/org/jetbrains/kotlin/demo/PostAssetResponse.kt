package org.jetbrains.kotlin.demo

import java.net.URL

data class PostAssetResponse(val presignedUrl: URL, val assetId: String)
