package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class Direction(@Json(name = "routes")
                     val routes: List<RoutesItem>?,
                     @Json(name = "geocoded_waypoints")
                     val geocodedWaypoints: List<GeocodedWaypointsItem>?,
                     @Json(name = "status")
                     val status: String = "")