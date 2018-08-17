package me.hiennguyen.uberux.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Gravity.END
import android.view.animation.DecelerateInterpolator
import kotlinx.android.synthetic.main.activity_password.*
import me.hiennguyen.uberux.R
import me.hiennguyen.uberux.utils.hideKeyboard

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
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

        fabProgressCircle.setOnClickListener {
            etPass.isCursorVisible = false
            rootFrame.alpha = 0.4f
            fabProgressCircle.show()
            etPass.hideKeyboard()
            Handler().postDelayed({
                val intent = Intent(this@PasswordActivity, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }

        ivback.setOnClickListener {
            onBackPressed()
        }
    }
}