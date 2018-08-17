package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class StepsItem(@Json(name = "duration")
                     val duration: Duration,
                     @Json(name = "start_location")
                     val startLocation: StartLocation,
                     @Json(name = "distance")
                     val distance: Distance,
                     @Json(name = "travel_mode")
                     val travelMode: String = "",
                     @Json(name = "html_instructions")
                     val htmlInstructions: String = "",
                     @Json(name = "end_location")
                     val endLocation: EndLocation,
                     @Json(name = "polyline")
                     val polyline: Polyline)