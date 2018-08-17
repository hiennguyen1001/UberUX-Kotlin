package me.hiennguyen.uberux.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
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
import kotlinx.android.synthetic.main.activity_login_with_phone.*
import me.hiennguyen.uberux.utils.Constants.ANIM_DURATION
import me.hiennguyen.uberux.R
import me.hiennguyen.uberux.utils.hideKeyboard
import me.hiennguyen.uberux.utils.showKeyboard

class LoginPhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_phone)
        setupWindowAnimations()
        ivback.setOnClickListener {
            super.onBackPressed()
        }
        fabProgressCircle.setOnClickListener {
            etPhoneNo.isCursorVisible = false
            rootFrame.alpha = 0.4f
            fabProgressCircle.show()

            Handler().postDelayed({
                val intent = Intent(this, PasswordActivity::class.java)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                startActivity(intent, options.toBundle())
            }, 1000)
        }
        ivback.imageAlpha = 0
    }

    private fun setupWindowAnimations() {
        // enterTransition
        with(ChangeBounds()) {
            duration = ANIM_DURATION
            interpolator = DecelerateInterpolator(4F)
            addListener(onStart = {
                ivback.imageAlpha = 0
            }, onEnd = {
                etPhoneNo.showKeyboard()
                ivback.imageAlpha = 255
                ivback.startAnimation(
                    AnimationUtils.loadAnimation(
                        this@LoginPhoneActivity,
                        R.anim.slide_right
                    )
                )
            })
            window.sharedElementEnterTransition = this
        }

        // returnTransition
        with(ChangeBounds()) {
            duration = ANIM_DURATION
            doOnStart {
                etPhoneNo.hideKeyboard()
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
            window.sharedElementReturnTransition = this
        }

        // exitTransition
        with(Slide(START)) {
            duration = 700
            addTarget(R.id.llphone)
            interpolator = DecelerateInterpolator()
            doOnStart {
                rootFrame.alpha = 1f
                fabProgressCircle.hide()
                llphone.setBackgroundColor(Color.parseColor("#EFEFEF"))
            }
            window.exitTransition = this
        }

        // ReenterTransition
        with(Slide(START)) {
            duration = 700
            interpolator = DecelerateInterpolator(2F)
            addTarget(R.id.llphone)
            doOnEnd {
                llphone.setBackgroundColor(Color.parseColor("#FFFFFF"))
                etPhoneNo.isCursorVisible = true
                etPhoneNo.showKeyboard()
            }
            window.reenterTransition = this
        }
    }
}