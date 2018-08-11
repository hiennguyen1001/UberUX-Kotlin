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
## How to use this project

Add your [GoogleMaps](https://developers.google.com/maps/documentation/android-api/) and [GooglePlaces](https://developers.google.com/places/android-api/) key to google_maps_api.xml and turn on direction api from developer console -> You are good to go !


