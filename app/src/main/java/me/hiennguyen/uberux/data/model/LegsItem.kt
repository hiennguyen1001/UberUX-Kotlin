package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class LegsItem(@Json(name = "duration")
                    val duration: Duration,
                    @Json(name = "start_location")
                    val startLocation: StartLocation,
                    @Json(name = "distance")
                    val distance: Distance,
                    @Json(name = "start_address")
                    val startAddress: String = "",
                    @Json(name = "end_location")
                    val endLocation: EndLocation,
                    @Json(name = "end_address")
                    val endAddress: String = "",
                    @Json(name = "steps")
                    val steps: List<StepsItem>?)