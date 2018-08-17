package me.hiennguyen.uberux.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.transition.doOnResume
import androidx.core.transition.doOnStart
import kotlinx.android.synthetic.main.activity_login.*
import me.hiennguyen.uberux.utils.Constants
import me.hiennguyen.uberux.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupWindowAnimations()
        DisplayMetrics().run {
            windowManager?.defaultDisplay?.getMetrics(this)
            ivUberLogo.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (0.65 * this.heightPixels).toInt()
            )
        }
        ivback.imageAlpha = 0
        llphone.setOnClickListener(this@LoginActivity)
        ivFlag.setOnClickListener(this@LoginActivity)
        tvPhoneNo.setOnClickListener(this@LoginActivity)
    }

    private fun setupWindowAnimations() {
        // exit window transaction
        with(ChangeBounds()) {
            duration = Constants.ANIM_DURATION
            window.sharedElementExitTransition = this
        }

        // reenter window transaction
        with(ChangeBounds()) {
            duration = Constants.ANIM_DURATION
            interpolator = DecelerateInterpolator(4F)
            doOnStart {
                AnimatorSet().apply {
                    playTogether(ObjectAnimator.ofFloat(tvMoving, "alpha", 0f, 1f))
                    duration = 800
                }.start()
            }
            doOnResume {
                tvMoving.alpha = 1F
            }
            window.sharedElementReenterTransition = this
        }
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, LoginPhoneActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(ivback, getString(R.string.transition_arrow)),
            Pair(ivFlag, getString(R.string.transition_ivFlag)),
            Pair(tvCode, getString(R.string.transition_tvCode)),
            Pair(tvPhoneNo, getString(R.string.transition_tvPhoneNo)),
            Pair(llphone, getString(R.string.transition_llPhone))
        )
        startActivity(intent, options.toBundle())
    }
}
