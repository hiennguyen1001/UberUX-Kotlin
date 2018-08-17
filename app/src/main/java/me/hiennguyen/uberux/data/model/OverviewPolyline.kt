package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class OverviewPolyline(@Json(name = "points")
                            val points: String = "")