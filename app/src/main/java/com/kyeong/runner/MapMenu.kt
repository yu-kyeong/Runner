package com.kyeong.runner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.SettingsSlicesContract.KEY_LOCATION
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map_menu.*


class MapMenu : Fragment() , OnMapReadyCallback {

    val permission_list: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //Location , map
    lateinit var map: GoogleMap
    lateinit var mLocation : Location
    lateinit var mCameraPosition : CameraPosition
    lateinit var locationManager: LocationManager

    lateinit var mapView: MapView

    //러닝 시작유무
    var runState: Boolean = false

    //현재 위치
    var position: LatLng = LatLng(0.0, 0.0)
    var startPos: LatLng = LatLng(0.0, 0.0)
    var endPos: LatLng = LatLng(0.0, 0.0)
    var defaultLocation : LatLng = LatLng(37.0,126.0)

    //marker
    lateinit var marker: Marker

    lateinit var mContext : FragmentActivity

    override fun onAttach(activity: Activity) {
        Log.d("MapMenu", "onAttach")
        //Fragment가 Activity에 attach 될 때 호출된다.
        mContext = activity as FragmentActivity
        super.onAttach(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MapMenu", "onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("MapMenu", "onSaveInstanceState")
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MapMenu", "onCreateView")
        val view : View = inflater.inflate(R.layout.fragment_map_menu, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list.toTypedArray(), 0)
        }else{
            //init()
        }

        mapView = view.findViewById(R.id.map)
        if (mapView != null){
            mapView.onCreate(savedInstanceState)
        }
        mapView.getMapAsync(this)

        return view
    }

 /*   private fun init(){
        Log.d("MapMenu", "init")
        val fragmentManager : FragmentManager? = getFragmentManager()
        val mapFragment: SupportMapFragment =
            fragmentManager?.findFragmentById(R.id.map) as SupportMapFragment

    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState)
        Log.d("MapMenu", "onActivityCreated")
        //Activity가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(mContext)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("MapMenu", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return
            }
        }
        //init()
    }





        override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap
            Log.d("MapMenu", "onMapReady")
            location_click.setOnClickListener {

            }
            start_btn.setOnClickListener {
                Log.d("MapMenu", "RunningState : $runState ")
                runningState()
            }
            getMyLocation()


     }
    
    fun setMyLocation(location: Location){

        Log.d("MapMenu", "setMyLocation")

        Log.d("MapMenu", "위도 : ${location.latitude}")
        Log.d("MapMenu", "경도 : ${location.longitude}")

        position = LatLng(location.latitude, location.longitude)

        val update1 : CameraUpdate = CameraUpdateFactory.newLatLng(position)
        var update2 : CameraUpdate = CameraUpdateFactory.zoomTo(15f)

        map.moveCamera(update1)
        map.animateCamera(update2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                return
            }
        }

        //현재위치 표시
        map.isMyLocationEnabled = true

        location_click.setOnClickListener {
            if (map.isMyLocationEnabled){
                map.setOnMyLocationButtonClickListener { false }
                map.isMyLocationEnabled = false
            }else{
                map.setOnMyLocationButtonClickListener { true }
                map.isMyLocationEnabled = true
            }
        }

        if(marker != null){
            marker.remove()
        }
        //var markerOptions : MarkerOptions
        marker.position
    }

    private fun getMyLocation(){

        Log.d("MapMenu", "getMyLocation")

        if (ContextCompat.checkSelfPermission(mContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            return
        }

        //이전에 측정한 값을 가져온다
        val locationG : Location? =
           locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var locationW : Location? =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (locationG != null){
            Log.d("MapMenu", "locationG : " + locationG.toString())
            setMyLocation(locationG)
        }else{
            if (locationW != null){
                Log.d("MapMenu", "locationW : " + locationG.toString())
                setMyLocation(locationW)
            }
        }

        //새롭게 측정한다
        val listener = GetMyLocationListener()
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("MapMenu", "GPS")
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 10 , 10f , listener)
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d("MapMenu", "Network")
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 , 10f , listener)
        }

    }

    fun runningState(){
        if (!runState){
            Toast.makeText(mContext, "러닝 시작", Toast.LENGTH_SHORT)
            runState = true

        }else {
            Toast.makeText(mContext,"러닝 종료", Toast.LENGTH_SHORT)
            runState = false
        }
    }

    inner class GetMyLocationListener : LocationListener{
        override fun onLocationChanged(p0: Location) {
            setMyLocation(p0)
            Log.d("MapMenu","onLocationChanged - Latitude : ${p0.latitude}")
            Log.d("MapMenu", "onLocationChanged - Longitude : ${p0.longitude}")

            //현재 위치 찾기
            locationManager.removeUpdates(this)
            var lat = p0.latitude
            var lon = p0.longitude
            position = LatLng(lat,lon)
            Log.d("MapMenu", "position : $position")

            //marker 위치 정하기
            marker = map.addMarker(
                MarkerOptions()
                    .position(position)
            )
            if (marker != null){
                Log.d("MapMenu", "marker position1 : " + marker.position.toString())
                marker.remove()
                val markerOptions = MarkerOptions()
                markerOptions.position(position)
                marker = map.addMarker(markerOptions)
                Log.d("MapMenu", "marker position2 : " + marker.position.toString())

            }

            //카메라 위치 정하기
            val movePosition: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(lat,lon), 18f)
            map.animateCamera(movePosition)

            //달리기 T/F
            if (runState){
                Log.d("MapMenu", "startPos if : $startPos")
                endPos = LatLng(lat, lon)

                startPos = LatLng(lat,lon)

                Thread.sleep(300)
            }else{
                Log.d("MapMenu", "startPos else : $startPos")
                startPos = LatLng(lat,lon)
            }

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }
    

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }



}