package com.example.ppd

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.show_position.*

open class MapsActivity : AppCompatActivity(), OnMapReadyCallback,  GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastlocation: Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    companion object{
        private  const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_position)


        imagePro.setOnClickListener {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        logoutPro.setOnClickListener {
            sharedPref.edit().remove("Email").apply()
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getInt("distance")
            distance2(value)
        }

        //just a test for now
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
              return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) {
            location ->
            if(location != null){
                lastlocation = location
                val currentLatLog = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLog)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLog,12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLog: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLog)
        markerOptions.title("$currentLatLog")
        mMap.addMarker((markerOptions))
        (mMap.addMarker((markerOptions)))?.showInfoWindow()

    }

    override fun onMarkerClick(p0: Marker) = false


    fun distance2(kilometre: Int){


        textSituation.visibility = View.VISIBLE
        if(kilometre > 2){
            textSituation.setText("Vous n'êtes pas chez vous !")
        }
        else{
            textSituation.setText("Vous êtes chez vous !")
        }

    }
}