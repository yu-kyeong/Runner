package com.kyeong.runner

import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_tab_layout.*

class TabLayoutActivity : AppCompatActivity() {

    lateinit var map: GoogleMap
    lateinit var locationManager: LocationManager

    //탭 hide
    var tabState : Boolean = false

    //위치
    var position: LatLng = LatLng(0.0, 0.0)

    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout)


        //supportFragmentManager.beginTransaction().add(R.id.pager, TimeLineMenu()).commit()
        val pagerAdapter = FragmentPagerAdapter(supportFragmentManager , 4)
        pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(pager)


        /*tab_layout.addOnTabSelectedListener( object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var pos = tab?.position
                Log.d("TabLayoutActivity", pos.toString())
                if (pos == 0 ){
                    fragment = TimeLineMenu()
                }else if (pos == 1){
                    fragment = MapMenu()
                }else if (pos == 2){
                    fragment = ActiveMenu()
                }else if (pos == 3){
                    fragment = ProfileMenu()
                }else{
                    fragment = MapMenu()
                }
                Log.d("TabLayoutActivity", fragment.toString())
                supportFragmentManager.beginTransaction().replace(R.id.pager, fragment).commit()

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })*/



       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list.toTypedArray(), 0)
        } else {
            init()
        }*/
    }

    fun tabState(){
        val pagerAdapter = FragmentPagerAdapter(supportFragmentManager , 4)
        pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(pager)

        //var pager : LayoutParams


        if (tabState) {
            tab_layout.visibility = View.GONE
        }else{
            tab_layout.visibility = View.VISIBLE
        }
    }

    class FragmentPagerAdapter(fragmentManager: FragmentManager,
                                val tabCount: Int): FragmentStatePagerAdapter(fragmentManager){
        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return TimeLineMenu()
                1 -> return MapMenu()
                2 -> return ActiveMenu()
                3 -> return ProfileMenu()
                else -> return TimeLineMenu()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "TimeLine"
                1 -> return "Running"
                2 -> return "Active"
                3 -> return "etc"
                else -> return "TimeLine"
            }
        }

        override fun getCount(): Int {
            return tabCount
        }
    }
}