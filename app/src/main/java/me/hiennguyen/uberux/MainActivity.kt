package me.hiennguyen.uberux

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.databinding.DataBindingUtil
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
import me.hiennguyen.uberux.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupWindowAnimations()
        DisplayMetrics().run {
            windowManager?.defaultDisplay?.getMetrics(this)
            mBinding.ivUberLogo.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (0.65 * this.heightPixels).toInt()
            )
        }
        mBinding.ivback.imageAlpha = 0

        with(mBinding) {
            ivback.imageAlpha = 0
            llphone.setOnClickListener(this@MainActivity)
            ivFlag.setOnClickListener(this@MainActivity)
            tvPhoneNo.setOnClickListener(this@MainActivity)
        }
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
                    playTogether(ObjectAnimator.ofFloat(mBinding.tvMoving, "alpha", 0f, 1f))
                    duration = 800
                }.start()
            }
            doOnResume {
                mBinding.tvMoving.alpha = 1F
            }
            window.sharedElementReenterTransition = this
        }
    }

    override fun onClick(v: View?) {
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
}
