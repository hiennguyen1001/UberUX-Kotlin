package me.hiennguyen.uberux.presentation

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.JointType.ROUND
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.SquareCap
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import me.hiennguyen.uberux.R
import me.hiennguyen.uberux.data.Repository
import me.hiennguyen.uberux.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import me.hiennguyen.uberux.utils.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private var mMap: GoogleMap? = null
    private lateinit var userLocation: Location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var destination: LatLng? = null
    private val listLatLng = ArrayList<LatLng>()
    private var blackPolyLine: Polyline? = null
    private var greyPolyLine: Polyline? = null
    private lateinit var argbEvaluator: ArgbEvaluator
    private lateinit var polyLineAnimator: ValueAnimator
    private var targetMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        argbEvaluator = ArgbEvaluator()
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val devWidth = displayMetrics.widthPixels

        with(viewPager) {
            clipToPadding = false
            pageMargin = -devWidth / 2
            setPageTransformer(true, pageTransformer)
            adapter = UberCarsPagerAdapter(Arrays.asList(0, 1))
        }
        ivHome.setOnClickListener(this)
        rlwhere.setOnClickListener(this)
        polyLineAnimator = initAnimatePolyline()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setMaxZoomPreference(20f)

        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style)
        googleMap?.setMapStyle(style)
        if (checkPermission()) {
            onLocationPermissionGranted()
        }
        startRevealAnimation()
    }

    override fun onBackPressed() {
        if (viewPager.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(rootFrame)
            viewPager.visibility = View.INVISIBLE
            mMap?.setPadding(0, 0, 0, 0)
            ivHome.visibility = View.VISIBLE
            rlwhere.visibility = View.VISIBLE
            return
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivHome -> showViewPagerWithTransition()
            R.id.rlwhere -> openPlaceAutoCompleteView()
        }
    }

    private fun showViewPagerWithTransition() {
        TransitionManager.beginDelayedTransition(rootFrame)
        viewPager.visibility = View.VISIBLE
        ivHome.visibility = View.INVISIBLE
        rlwhere.visibility = View.INVISIBLE

        mMap?.setPadding(0, 0, 0, viewPager.height)
    }

    private fun startRevealAnimation() {

        val cx = rootFrame.measuredWidth / 2
        val cy = rootFrame.measuredHeight / 2
        ViewAnimationUtils.createCircularReveal(rootll, cx, cy, 50f, rootFrame.width.toFloat()).apply {
            duration = 500
            interpolator = AccelerateInterpolator(2f)
            doOnEnd {
                rlwhere.visibility = View.VISIBLE
                ivHome.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data)
                    destination = place.latLng
                    listLatLng.clear()
                    mMap?.clear()
                    setUpPolyLine()
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data)
                    Toast.makeText(this, "Error $status", Toast.LENGTH_SHORT).show()
                    polyLineAnimator.start()
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                    polyLineAnimator.start()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkPermission()) {
                onLocationPermissionGranted()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationPermissionGranted() {
        if (!checkPermission()) return

        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.isMyLocationEnabled = true
        mFusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                userLocation = location

                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(userLocation.latitude, userLocation.longitude))
                    .zoom(17f)
                    .build()

                addOverlay(LatLng(userLocation.latitude, userLocation.longitude))
                mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }
    }

    private fun openPlaceAutoCompleteView() {
        polyLineAnimator.pause()
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
        }
    }

    private fun checkPermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            //Ask for the permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            Toast.makeText(this, "Please give location permission", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setUpPolyLine() {
        destination?.let {
            Repository.getPolyLineData(LatLng(userLocation.latitude, userLocation.longitude), it) {
                Single.just(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this@MapsActivity::drawPolyline)
            }
        } ?: Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        var bitmap: Bitmap? = null
        drawable?.run {
            if (this is BitmapDrawable) {
                if (bitmap != null) {
                    return bitmap
                }
            }
            bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }

            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return bitmap
    }

    private fun addOverlay(place: LatLng) {

        val groundOverlay = mMap?.addGroundOverlay(
            GroundOverlayOptions()
                .position(place, 100f)
                .transparency(0.5f)
                .zIndex(3f)
                .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getDrawable(R.drawable.map_overlay))))
        )

        startOverlayAnimation(groundOverlay)
    }

    private fun startOverlayAnimation(groundOverlay: GroundOverlay?) {

        val animatorSet = AnimatorSet()

        val vAnimator = ValueAnimator.ofInt(0, 100).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                groundOverlay?.setDimensions((valueAnimator.animatedValue as Int).toFloat())
            }
        }

        val tAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                groundOverlay?.transparency = valueAnimator.animatedValue as Float
            }
        }

        animatorSet.duration = 3000
        animatorSet.playTogether(vAnimator, tAnimator)
        animatorSet.start()
    }

    private val pageTransformer: ViewPager.PageTransformer =
        ViewPager.PageTransformer { page, position ->
            if (position < -1) { // [-Infinity,-1)
            } else if (position <= 1) { // [-1,1]

                if (position >= -1 && position < 0) {

                    val uberEco = page.findViewById<View>(R.id.lluberEconomy) as? LinearLayout
                    val uberEcoTv = page.findViewById<View>(R.id.tvuberEconomy) as? TextView
                    if (uberEcoTv != null && uberEco != null) {
                        uberEcoTv.setTextColor(
                            argbEvaluator.evaluate(
                                -2 * position,
                                resources.getColor(R.color.black),
                                resources.getColor(R.color.grey)
                            ) as Int
                        )

                        uberEcoTv.textSize = 16 + 4 * position
                        uberEco.x = page.width * position
                    }
                } else if (position in 0.0..1.0) {
                    val uberPreTv = page.findViewById<View>(R.id.tvuberPre) as? TextView
                    val uberPre = page.findViewById<View>(R.id.llUberPre) as? LinearLayout
                    if (uberPreTv != null && uberPre != null) {
                        uberPreTv.setTextColor(
                            ArgbEvaluator().evaluate(
                                1 - position,
                                resources.getColor(R.color.grey),
                                resources.getColor(R.color.black)
                            ) as Int
                        )

                        uberPreTv.textSize = 12 + 4 * (1 - position)
                        uberPre.x = uberPre.left + page.width * position
                    }
                }
            }
        }

    private fun drawPolyline(result: List<List<HashMap<String, String>>>) {

        var points: ArrayList<LatLng>
        var latLngBounds = LatLngBounds.Builder()

        // Traversing through all the routes
        for (i in result.indices) {
            points = ArrayList()

            // Fetching i-th route
            val path = result[i]

            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]

                val lat = point["lat"]?.toDouble()
                val lng = point["lng"]?.toDouble()
                if (lat != null && lng != null) {
                    with(LatLng(lat, lng)) {
                        points.add(this)
                        latLngBounds.include(this)
                    }
                }
            }

            listLatLng.addAll(points)
        }
        with(PolylineOptions()) {
            width(10f)
            color(Color.BLACK)
            startCap(SquareCap())
            endCap(SquareCap())
            jointType(ROUND)
            blackPolyLine = mMap?.addPolyline(this)
        }

        with(PolylineOptions()) {
            width(10f)
            color(Color.GRAY)
            startCap(SquareCap())
            endCap(SquareCap())
            jointType(ROUND)
            greyPolyLine = mMap?.addPolyline(this)
        }
        polyLineAnimator.start()
        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 100))
    }

    private fun initAnimatePolyline() = ValueAnimator.ofInt(0, 100).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        addUpdateListener { animator ->
            blackPolyLine?.run {
                val listPoint = points
                val initialPointSize = listPoint.size
                val animatedValue = animator.animatedValue as Int
                val newPoints = (animatedValue * listLatLng.size) / 100

                if (initialPointSize < newPoints) {
                    listPoint.addAll(listLatLng.subList(initialPointSize, newPoints))
                    points = listPoint
                }
            }
        }
        addListener(onStart = {
            with(MarkerOptions()) {
                position(listLatLng[listLatLng.size - 1])
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                targetMarker = mMap?.addMarker(this)
            }
        }, onEnd = {
            val blackLocal = blackPolyLine
            val greyLocal = greyPolyLine
            if (blackLocal != null && greyLocal != null) {
                val blackLatLng = blackLocal.points
                val greyLatLng = greyLocal.points

                greyLatLng.clear()
                greyLatLng.addAll(blackLatLng)
                blackLatLng.clear()

                blackLocal.points = blackLatLng
                greyLocal.points = greyLatLng
                blackLocal.zIndex = 2f
            }
            start()
        })
    }
}