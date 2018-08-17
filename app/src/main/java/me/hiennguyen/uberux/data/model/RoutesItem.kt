package me.hiennguyen.uberux.data.model

import com.squareup.moshi.Json

data class RoutesItem(@Json(name = "summary")
                      val summary: String = "",
                      @Json(name = "copyrights")
                      val copyrights: String = "",
                      @Json(name = "legs")
                      val legs: List<LegsItem>?,
                      @Json(name = "bounds")
                      val bounds: Bounds,
                      @Json(name = "overview_polyline")
                      val overviewPolyline: OverviewPolyline)