package me.hiennguyen.uberux

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Gravity.END
import android.view.animation.DecelerateInterpolator
import me.hiennguyen.uberux.databinding.ActivityPasswordBinding

class PasswordActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_password)
        with(Slide(END)) {
            duration = 700
            addTarget(R.id.llphone)
            interpolator = DecelerateInterpolator(2f)
            window.enterTransition = this
        }

        with(Slide(END)) {
            duration = 700
            addTarget(R.id.llphone)
            interpolator = DecelerateInterpolator(2f)
            window.returnTransition = this
        }

        mBinding.fabProgressCircle.setOnClickListener {
            with(mBinding) {
                etPass.isCursorVisible = false
                rootFrame.alpha = 0.4f
                fabProgressCircle.show()
                etPass.hideKeyboard()
            }
            Handler().postDelayed({
                val intent = Intent(this@PasswordActivity, PasswordActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }

        mBinding.ivback.setOnClickListener {
            onBackPressed()
        }
    }
}