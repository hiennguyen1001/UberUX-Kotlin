package me.hiennguyen.uberux

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity.START
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.transition.addListener
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import me.hiennguyen.uberux.Constants.ANIM_DURATION
import me.hiennguyen.uberux.databinding.ActivityLoginWithPhoneBinding

class LoginPhoneActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLoginWithPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_phone)
        setupWindowAnimations()
        mBinding.ivback.setOnClickListener {
            super.onBackPressed()
        }
        mBinding.fabProgressCircle.setOnClickListener {
            mBinding.etPhoneNo.isCursorVisible = false
            mBinding.rootFrame.alpha = 0.4f
            mBinding.fabProgressCircle.show()

            Handler().postDelayed({
                mBinding.fabProgressCircle.hide()
            }, 1000)
        }
        mBinding.ivback.imageAlpha = 0
    }

    private fun setupWindowAnimations() {
        // enterTransition
        with(ChangeBounds()) {
            duration = ANIM_DURATION
            interpolator = DecelerateInterpolator(4F)
            addListener(onStart = {
                mBinding.ivback.imageAlpha = 0
            }, onEnd = {
                mBinding.etPhoneNo.showKeyboard()
                with(mBinding) {
                    ivback.imageAlpha = 255
                    ivback.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@LoginPhoneActivity,
                            R.anim.slide_right
                        )
                    )
                }
            })
            window.sharedElementEnterTransition = this
        }

        // returnTransition
        with(ChangeBounds()) {
            duration = ANIM_DURATION
            doOnStart {
                mBinding.etPhoneNo.hideKeyboard()
                with(mBinding) {
                    tvMoving.text = null
                    tvMoving.hint = getString(R.string.enter_no)
                    ivFlag.imageAlpha = 0
                    tvCode.alpha = 0f
                    etPhoneNo.visibility = View.GONE
                    etPhoneNo.isCursorVisible = false
                    etPhoneNo.background = null
                    val animation =
                        AnimationUtils.loadAnimation(this@LoginPhoneActivity, R.anim.slide_left)
                    ivback.startAnimation(animation)
                }
            }
            window.sharedElementReturnTransition = this
        }

        // exitTransition
        with(Slide(START)) {
            duration = 700
            addTarget(R.id.llphone)
            interpolator = DecelerateInterpolator()
            doOnStart {
                with(mBinding) {
                    rootFrame.alpha = 1f
                    fabProgressCircle.hide()
                    llphone.setBackgroundColor(Color.parseColor("#EFEFEF"))
                }
            }
            window.exitTransition = this
        }

        // ReenterTransition
        with(Slide(START)) {
            duration = 700
            interpolator = DecelerateInterpolator(2F)
            addTarget(R.id.llphone)
            doOnEnd {
                with(mBinding) {
                    llphone.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    etPhoneNo.isCursorVisible = true
                    etPhoneNo.showKeyboard()
                }
            }
            window.reenterTransition = this
        }
    }
}