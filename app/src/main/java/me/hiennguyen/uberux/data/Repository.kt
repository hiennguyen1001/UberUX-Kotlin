package me.hiennguyen.uberux.data

import com.google.android.gms.maps.model.LatLng
import me.hiennguyen.uberux.data.model.Direction
import me.hiennguyen.uberux.utils.decodePolyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

object Repository {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/directions/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    private val service: DirectionService by lazy {
        retrofit.create(DirectionService::class.java)
    }

    fun getPolyLineData(source: LatLng, destination: LatLng, callBack: (List<List<HashMap<String, String>>>) -> Unit) {
        service.getPolylineData(
            "${source.latitude},${source.longitude}",
            "${destination.latitude},${destination.longitude}"
        ).enqueue(object : Callback<Direction> {
            override fun onResponse(call: Call<Direction>?, response: Response<Direction>?) {
                if (response != null && response.isSuccessful && response.body() != null) {
                    callBack(parse(response.body()!!))
                }
            }

            override fun onFailure(call: Call<Direction>?, t: Throwable?) {
            }
        })
    }

    fun parse(data: Direction): List<List<HashMap<String, String>>> {
        val routes = ArrayList<List<HashMap<String, String>>>()
        data.routes?.apply {
            for (i in 0 until size) {
                val path = ArrayList<HashMap<String, String>>()
                get(i).legs?.apply {
                    for (j in 0 until size) {
                        get(i).steps?.apply {
                            for (k in 0 until size) {
                                val list = get(k).polyline.points.decodePolyline()
                                for (l in list.indices) {
                                    val hm = HashMap<String, String>()
                                    hm["lat"] = list[l].latitude.toString()
                                    hm["lng"] = list[l].longitude.toString()
                                    path.add(hm)
                                }
                            }
                        }
                        routes.add(path)
                    }
                }
            }
        }
        return routes
    }
}