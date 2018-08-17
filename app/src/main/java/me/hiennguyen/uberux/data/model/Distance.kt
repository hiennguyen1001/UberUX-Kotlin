package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class Distance(@Json(name = "text")
                    val text: String = "",
                    @Json(name = "value")
                    val value: Int = 0)