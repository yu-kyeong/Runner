package com.kyeong.runner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity() {

    val permission_list: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var map: GoogleMap
    lateinit var locationManager: LocationManager

    //러닝 시작유무
    var runState: Boolean = false

    //위치
    var position: LatLng = LatLng(0.0, 0.0)
    var startPos: LatLng = LatLng(0.0, 0.0)
    var endPos: LatLng = LatLng(0.0, 0.0)

    lateinit var marker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("test123", "if 지나가는중")
            requestPermissions(permission_list.toTypedArray(), 0)
        } else {
            Log.d("test123", "else 지나가는중")
            init()
        }


        /*val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()*/

        /*val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)*/


    }

    override fun onRequestPermissionsResult(
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

    /*   MapActivity : AppCompatActivity() , OnMap.. 으로 사용할 때

        override fun onMapReady(p0: GoogleMap) {
        Log.d("test", "onMapReady")
        val SEOUL : LatLng = LatLng(37.56, 126.97)
        p0.addMarker(
            MarkerOptions()
                .position(SEOUL)
                .title("서울")
        )
        p0.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10.0F))

    }*/
    inner class MapReadyCallback : OnMapReadyCallback {
        override fun onMapReady(p0: GoogleMap) {
            Log.d("test", "onMapReady")
            map = p0

            val startBtn: Button = findViewById(R.id.start_btn)
            startBtn.setOnClickListener {
                Log.d("runState", runState.toString())
                runningState()

            }

            getMyLocation()


        }
    }

    //움직이는 위치 그리기
    fun drawPath() {
        //startPos = LatLng(position.latitude, position.longitude)
        Log.d("runState", "Draw")
        val option: PolylineOptions = PolylineOptions()
            .add(startPos).add(endPos).color(Color.BLUE).width(12f).geodesic(true)
        map.addPolyline(option)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, 18f))

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

    private fun testRunning() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 권한 확인 작업
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        var location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {

            var testLat = location.latitude
            var testLon = location.longitude

            val movePosition: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(testLat, testLon), 18f)
            map.animateCamera(movePosition)
            if (runState) {
                Log.d("test", "Main 지나가나용")
                Log.d("test", "Lat : $testLat" + " Lon : $testLon")

                endPos = LatLng(testLat, testLon)
                testLat += 20.0
                testLon += 20.0
                //position = LatLng(testLat, testLon)
                drawPath()
                startPos = LatLng(testLat, testLon)


            }
        }
    }

    fun runningState() {

        if (!runState) {
            Log.d("runState", runState.toString())
            Toast.makeText(this@MapActivity, "러닝 시작", Toast.LENGTH_SHORT).show()
            runState = true
            map.isMyLocationEnabled

        } else {
            Log.d("runState", runState.toString())
            Toast.makeText(this@MapActivity, "러닝 종료", Toast.LENGTH_SHORT).show()
            runState = false
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
            marker = map.addMarker(
                MarkerOptions()
                    .position(position)
            )

            if (marker != null) {
                Log.d("test", "marker1 : " + marker.position.toString())
                marker.remove()
                val markerOptions: MarkerOptions = MarkerOptions()
                markerOptions.position(position)
                marker = map.addMarker(markerOptions)
                Log.d("test", "marker2 : " + marker.position.toString())
            }

            val movePosition: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 18f)
            map.animateCamera(movePosition)
            if (runState) {
                Log.d("test", "if 지나가나용")
                Log.d("test", "Lat : $lat" + " Lon : $lon")

                endPos = LatLng(lat, lon)
                drawPath()
                startPos = LatLng(lat, lon)

                Thread.sleep(300)
            } else {
                Log.d("test", "else 지나가나용")
                startPos = LatLng(lat, lon)
                Log.d("test", "startPos : $startPos")
            }


        }



        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }

    }



}