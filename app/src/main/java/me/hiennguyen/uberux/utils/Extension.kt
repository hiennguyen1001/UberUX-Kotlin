package me.hiennguyen.uberux.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.model.LatLng
import java.util.*

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}

fun String.decodePolyline(): List<LatLng> {

    val poly = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < length) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = get(index++).toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = get(index++).toInt() - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5
        )
        poly.add(p)
    }

    return poly
}