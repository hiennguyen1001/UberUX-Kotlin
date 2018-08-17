package me.hiennguyen.uberux.data

import me.hiennguyen.uberux.data.model.Direction
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionService {

    @GET("json")
    fun getPolylineData(@Query("origin") origin: String, @Query("destination") destination: String): Call<Direction>
}