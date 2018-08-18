# UberUX-Kotlin

### Project that demonstrates the entire animation stack present in the Uber app (android) using Kotlin language.


## Demo

![Demo](https://user-images.githubusercontent.com/12782512/28917901-e7eac470-7864-11e7-87f8-97227d75a721.gif)

## Libraries
1. [FabProgressCircle](https://github.com/JorgeCastilloPrz/FABProgressCircle)
2. [Retrofit](https://github.com/square/retrofit)
3. [RxAndroid](https://github.com/ReactiveX/RxAndroid)
4. [KotlinKtx](https://developer.android.com/kotlin/ktx)


## Concepts
**Transitions** -  For sharing elements between activities 
 ```
fun startTransition() {

         val intent = Intent(this, LoginPhoneActivity::class.java)
          val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair(mBinding.ivback, getString(R.string.transition_arrow)),
                    Pair(mBinding.ivFlag, getString(R.string.transition_ivFlag)),
                    Pair(mBinding.tvCode, getString(R.string.transition_tvCode)),
                    Pair(mBinding.tvPhoneNo, getString(R.string.transition_tvPhoneNo)),
                    Pair(mBinding.llphone, getString(R.string.transition_llPhone))
                )
           startActivity(intent, options.toBundle())


    }
```
**ViewPagerTransformer** - For performing animations when ViewPager is swiped
  
```
private val pageTransformer: ViewPager.PageTransformer =
        ViewPager.PageTransformer { page, position ->
            if (position < -1) { // [-Infinity,-1)
            
            } else if (position <= 1) { // [-1,1]
                // animate here
            }
        }
```


**Overlays** - For creating overlays on map

```
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

```
**ValueAnimator** - For animating overlays and polylines

 ```
val tAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                // animate in here
            }
        }

```

**PolyLines** - For drawing lines on map
```
 with(PolylineOptions()) {
             width(10f)
             color(Color.GRAY)
             startCap(SquareCap())
             endCap(SquareCap())
             jointType(ROUND)
             greyPolyLine = mMap?.addPolyline(this)
         }
```
## How to use this project

Add your [GoogleMaps](https://developers.google.com/maps/documentation/android-api/) and [GooglePlaces](https://developers.google.com/places/android-api/) key to google_maps_api.xml and turn on direction api from developer console -> You are good to go !


