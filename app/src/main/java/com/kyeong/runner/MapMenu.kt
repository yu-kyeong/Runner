package com.kyeong.runner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_tab_layout.*
import kotlinx.android.synthetic.main.fragment_map_menu.*
import kotlinx.android.synthetic.main.running_reset_dialog.view.*
import kotlinx.android.synthetic.main.running_stop_dialog.view.*
import kotlinx.android.synthetic.main.running_stop_dialog.view.keepRun_btn
import java.sql.Time
import java.util.*
import kotlin.concurrent.timer


class MapMenu : Fragment() , OnMapReadyCallback {

    val permission_list: List<String> = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    //Location , map
    lateinit var map: GoogleMap

    var locationManager: LocationManager? = null
    var locationListener : LocationListener? = null

    //inflate 될 view
    lateinit var mapView: MapView

    //러닝 시작유무
    var runState: Boolean = false
    //일시정지 T/F
    var pauseState : Boolean = false

    //현재 위치
    var position: LatLng = LatLng(0.0, 0.0)
    var startPos: LatLng = LatLng(0.0, 0.0)
    var endPos: LatLng = LatLng(0.0, 0.0)
    var defaultLocation : LatLng = LatLng(37.0,126.0)

    //marker
    var marker: Marker? = null

    //timer
    private var time = 0
    private var timerTask : Timer? = null

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

        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                var lat = 0.0
                var lon = 0.0
                if (p0 != null) {
                    lat = p0.latitude
                    lon = p0.longitude
                    Log.d("MapMenu", "location : $lat + $lon")
                }

                var currentLocation: LatLng = LatLng(lat, lon)

                //map.addMarker(MarkerOptions().position(currentLocation))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Log.d("MapMenu", "onMapReady")
        location_click.setOnClickListener {
            getMyLocation()
        }
        //타이머 시작
        start_btn.setOnClickListener {
            Log.d("MapMenu", "RunningState : $runState ")
            runningStart()
        }
        //타이머 종료
        stop_btn.setOnClickListener {
            Log.d("MapMenu","RunningStop")
            runningStop()
        }
        //일시정지
        pause_btn.setOnClickListener {
            Log.d("MapMenu","RunningPause")
            runningPause()
        }
        //초기화
        reset_btn.setOnClickListener {
            Log.d("MapMenu", "RunningReset")
            runningReset()
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

    }

    private fun getMyLocation(){

        Log.d("MapMenu", "getMyLocation")
        val listener = GetMyLocationListener()
        /*
        if (checkSelfPermission(mContext,Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000L,
                30f,
                listener
            )
        }*/

        // 권한 확인 작업
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        //이전에 측정한 값을 가져온다
        val locationG : Location? =
           locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var locationW : Location? =
            locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (locationG != null){
            Log.d("MapMenu", "locationG : $locationG")
            setMyLocation(locationG)
        }else{
            if (locationW != null){
                Log.d("MapMenu", "locationW : $locationW")
                setMyLocation(locationW)
            }
        }

        //새롭게 측정한다
        if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            Log.d("MapMenu", "GPS")
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER , 10 , 10f , listener)
        }
        if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true){
            Log.d("MapMenu", "Network")
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 , 10f , listener)
        }
    }

    //타이머 시작
    private fun runningStart(){
        Log.d("MapMenu","runningStart")
        timerTask = null
        if (!runState) {
            Log.d("MapMenu","runState = $runState")
            Toast.makeText(context, "러닝 시작", Toast.LENGTH_SHORT).show()
            runState = true
            stop_btn.visibility = View.VISIBLE
            start_btn.visibility = View.GONE
            timer_page.visibility = View.VISIBLE
            reset_btn.visibility = View.VISIBLE
            pause_btn.visibility = View.VISIBLE

            timerTask = timer(period = 10){
                time++

                val hour = (time / 144000) % 24 // 1시간
                val min = (time / 6000) % 60 // 1분
                val sec = (time / 100) % 60 // 1초
                //val mill = time % 100 // 0.01초

                mContext.runOnUiThread {
                    if (hour < 10){
                        hour_text.text = "0$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }else{
                        hour_text.text = "$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }

                }
            }//timerTask 끝
        }
            //Toast.makeText(context,"러닝 종료", Toast.LENGTH_SHORT).show()
    }

    //타이머 종료
    private fun runningStop(){
        Log.d("MapMenu","runningStop")
        Log.d("MapMenu","runState = $runState")

        //멈춘 시간 저장
        val lapTime = time

        //apply() 스코프 함수 TextView 를 생성과 동시에 초기화
       /* val textView = TextView(mContext).apply {
            textSize = 20f
            text = "${lapTime/100} : ${lapTime%100}" //출력할 시간
        }*/


        //val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = layoutInflater.inflate(R.layout.running_stop_dialog, null)
        val builder = AlertDialog.Builder(mContext)
            .create()
            builder.setView(dialogView)
            builder.show()

        dialogView.end_btn.setOnClickListener {
            stop_btn.visibility = View.GONE
            start_btn.visibility = View.VISIBLE
            timer_page.visibility = View.GONE
            builder.dismiss()
            runState = false
            timerTask?.cancel()

            //tab_layout.visibility = View.VISIBLE //tab 나타내기

            Log.d("MapMenu","LapTime = ${lapTime % 100} : ${lapTime / 100}")
        }

        dialogView.keepRun_btn.setOnClickListener {
            Log.d("MapMenu","keepRunning")
            builder.dismiss()
        }
    }
    //타이머 일시정지
    private fun runningPause(){
        val lapTime = time
        if (!pauseState) {
            timerTask?.cancel()
            pause_btn.text = "이어 달리기"
            pauseState = true
        }else{
            timerTask = timer(period = 10){
                time++

                val hour = (time / 144000) % 24 // 1시간
                val min = (time / 6000) % 60 // 1분
                val sec = (time / 100) % 60 // 1초
                //val mill = time % 100 // 0.01초

                mContext.runOnUiThread {
                    if (hour < 10){
                        hour_text.text = "0$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }else{
                        hour_text.text = "$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }

                }
            }//timerTask 끝

            pause_btn.text = "일시정지"
            pauseState = false

        }

    }

    //타이머 초기화
    private fun runningReset(){
        val dialogView2 = layoutInflater.inflate(R.layout.running_reset_dialog, null)
        val builder = AlertDialog.Builder(mContext)
            .create()
        builder.setView(dialogView2)
        builder.show()

        dialogView2.keepRun_btn.setOnClickListener {
            builder.dismiss()
        }
        dialogView2.reset_btn.setOnClickListener {
            timerTask?.cancel()
            time = 0

            timerTask = timer(period = 10){
                time++

                val hour = (time / 144000) % 24 // 1시간
                val min = (time / 6000) % 60 // 1분
                val sec = (time / 100) % 60 // 1초
                //val mill = time % 100 // 0.01초

                mContext.runOnUiThread {
                    if (hour < 10){
                        hour_text.text = "0$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }else{
                        hour_text.text = "$hour : "
                        if(min < 10){
                            min_text.text = "0$min : "
                        }else{
                            min_text.text = "$min : "
                        }
                        if (sec < 10){
                            sec_text.text = "0$sec"
                        }else{
                            sec_text.text = "$sec"
                        }
                    }

                }
            }//timerTask 끝



            builder.dismiss()
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

    inner class GetMyLocationListener : LocationListener{
        override fun onLocationChanged(p0: Location) {
            setMyLocation(p0)
            Log.d("MapMenu","onLocationChanged - Latitude : ${p0.latitude}")
            Log.d("MapMenu", "onLocationChanged - Longitude : ${p0.longitude}")

            //현재 위치 찾기
            locationManager?.removeUpdates(this)
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
                Log.d("MapMenu", "marker position1 : " + marker?.position.toString())
                marker?.remove()
                val markerOptions = MarkerOptions()
                markerOptions.position(position)
                marker = map.addMarker(markerOptions)
                Log.d("MapMenu", "marker position2 : " + marker?.position.toString())

            }

            //카메라 위치 정하기
            val movePosition: CameraUpdate =
                CameraUpdateFactory.newLatLngZoom(LatLng(lat,lon), 18f)
            map.animateCamera(movePosition)

            //달리기 T/F
            if (runState){
                Log.d("MapMenu", "startPos if : $startPos")
                endPos = LatLng(lat, lon)
                drawPath()
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