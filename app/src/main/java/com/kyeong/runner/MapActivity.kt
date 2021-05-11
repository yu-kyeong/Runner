package com.kyeong.runner

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng

class MapActivity : AppCompatActivity() {

    val permission_list : List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var map : GoogleMap
    lateinit var locationManager : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("test123","if 지나가는중")
            requestPermissions(permission_list.toTypedArray(), 0)
        } else {
            Log.d("test123","else 지나가는중")
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
        for ( result in grantResults ){
            if (result == PackageManager.PERMISSION_DENIED){
                return
            }
        }
        init()
    }

    private fun init(){
        Log.d("test", "init")
        val fragmentManager :FragmentManager = supportFragmentManager
        val mapFragment : SupportMapFragment = fragmentManager.findFragmentById(R.id.map) as SupportMapFragment

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
    inner class MapReadyCallback : OnMapReadyCallback{
        override fun onMapReady(p0: GoogleMap) {
            Log.d("test", "onMapReady")
            map = p0
            getMyLocation()
        }
    }

    //내위치 측정하기
    open fun setMyLocation(location : Location){
        Log.d("Location","위도 : "+location.latitude)
        Log.d("Location", "경도 : "+location.longitude)

        val position : LatLng = LatLng(location.latitude , location.longitude)

        val update1 : CameraUpdate = CameraUpdateFactory.newLatLng(position)
        val update2 : CameraUpdate = CameraUpdateFactory.zoomTo(15f)

        map.moveCamera(update1)
        map.animateCamera(update2)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                return
            }
        }

        //현재위치 표시
        map.isMyLocationEnabled

    }

    fun getMyLocation(){
        Log.d("test", "getMyLocation")

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // 권한 확인 작업
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                return
            }
        }

        //이전에 측정한 값을 가져온다
        val locationG : Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val locationW : Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (locationG != null){
            Log.d("test", "locationG : "+locationG.toString())
            setMyLocation(locationG)
        }else{
            if (locationW != null){
                Log.d("test", "locationW : "+locationG.toString())
                setMyLocation(locationW)
            }
        }

        //새롭게 측정한다
        val listener = GetMyLocationListener()

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            Log.d("test", "true ?")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 1000, 10f , listener)
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 1000, 10f , listener)
        }

    }

    inner class GetMyLocationListener : LocationListener {
        override fun onLocationChanged(p0: Location) {
            setMyLocation(p0)
            locationManager.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }
}