package com.kyeong.runner

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.activity_tab_layout.*

class TabLayoutActivity : AppCompatActivity() {

    lateinit var map: GoogleMap
    lateinit var locationManager: LocationManager

    //러닝 시작유무
    var runState: Boolean = false

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
            Log.d("test123", "if 지나가는중")
            requestPermissions(permission_list.toTypedArray(), 0)
        } else {
            Log.d("test123", "else 지나가는중")
            init()
        }*/
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

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("test", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return
            }
        }
        init()
    }

    private fun init() {
        Log.d("test", "init")
        val fragmentManager: FragmentManager = supportFragmentManager
        val mapFragment: SupportMapFragment =
            fragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val callback = MapReadyCallback()
        mapFragment.getMapAsync(callback)

    }

    inner class MapReadyCallback : OnMapReadyCallback {
        override fun onMapReady(p0: GoogleMap) {
            Log.d("test", "onMapReady")
            map = p0

            getMyLocation()


        }
    }

    fun getMyLocation() {
        Log.d("test", "getMyLocation")

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 권한 확인 작업
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        //이전에 측정한 값을 가져온다
        val locationG: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val locationW: Location? =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (locationG != null) {
            Log.d("test", "locationG : " + locationG.toString())
            setMyLocation(locationG)
        } else {
            if (locationW != null) {
                Log.d("test", "locationW : " + locationG.toString())
                setMyLocation(locationW)
            }
        }

        //새롭게 측정한다
        val listener = GetMyLocationListener()
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            Log.d("test", "true 1")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10f, listener)
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            Log.d("test", "true 2")
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10,
                10f,
                listener
            )
        }

    }

    //내위치 측정하기
    fun setMyLocation(location: Location) {
        Log.d("Location", "위도 : " + location.latitude)
        Log.d("Location", "경도 : " + location.longitude)

        position = LatLng(location.latitude, location.longitude)

        val update1: CameraUpdate = CameraUpdateFactory.newLatLng(position)
        val update2: CameraUpdate = CameraUpdateFactory.zoomTo(15f)

        map.moveCamera(update1)
        map.animateCamera(update2)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        //현재위치 표시
        map.isMyLocationEnabled = true

        val locationBtn: Button = findViewById(R.id.location_click)
        locationBtn.setOnClickListener {
            // map.setOnMyLocationClickListener {
            if (map.isMyLocationEnabled == true) {
                map.setOnMyLocationButtonClickListener { false }
                map.isMyLocationEnabled = false
            } else {
                map.setOnMyLocationButtonClickListener { true }
                map.isMyLocationEnabled = true
            }
            //   }
        }

    }


    inner class GetMyLocationListener : LocationListener {
        override fun onLocationChanged(p0: Location) {

            setMyLocation(p0)
            Log.d("test", "setMyLocation : ${p0.latitude} , ${p0.longitude}")
            locationManager.removeUpdates(this)
            var lat = p0.latitude
            var lon = p0.longitude
            position = LatLng(lat, lon)
            Log.d("test", "position : $position")


            val movePosition: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f)
            map.animateCamera(movePosition)



        }



        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }

    }*/



}