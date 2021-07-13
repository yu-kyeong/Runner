package com.kyeong.runner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.SettingsSlicesContract.KEY_LOCATION
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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

    //marker
    lateinit var marker: Marker

    private val KEY_LOCATION : String = "location"
    private val KEY_CAMERA_POSITION : String = "camera_position"

    lateinit var mContext : FragmentActivity

    override fun onAttach(activity: Activity) {
        //Fragment가 Activity에 attach 될 때 호출된다.
        mContext = activity as FragmentActivity
        super.onAttach(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.fragment_map_menu, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("test123", "if 지나가는중")
            requestPermissions(permission_list.toTypedArray(), 0)
        } else {
            Log.d("test123", "else 지나가는중")
        }

        mapView = view.findViewById(R.id.map)
        if (mapView != null){
            mapView.onCreate(savedInstanceState)
        }
        mapView.getMapAsync(this)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState)

        //Activity가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(mContext)


        /*fun createLocationRequest() {
            val locationRequest = LocationRequest.create()?.apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }*/

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
        //init()
    }




    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val myLocation : LatLng = LatLng(37.0, 127.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))


        start_btn.setOnClickListener {
            Log.d("runState", runState.toString())

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