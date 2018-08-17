package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class Bounds(@Json(name = "southwest")
                  val southwest: Southwest,
                  @Json(name = "northeast")
                  val northeast: Northeast)