package me.hiennguyen.uberux.presentation

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.hiennguyen.uberux.R

class UberCarsPagerAdapter(private val dataList: List<Int>) : PagerAdapter() {

    override fun getCount() = dataList.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.run {
            val view = LayoutInflater.from(context).inflate(
                if (dataList[position] == 0) R.layout.uber_economy else R.layout.uber_premium,
                this, false
            )
            addView(view)
            return view
        }
    }
}