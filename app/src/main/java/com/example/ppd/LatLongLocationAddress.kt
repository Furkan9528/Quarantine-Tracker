package com.example.ppd

import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_lat_long_location_address.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LatLongLocationAddress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lat_long_location_address)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        button.isEnabled = false

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                111)

        else
            button.isEnabled = true

        button.setOnClickListener {
            var city = editText.text.toString()
            var gc = Geocoder(this, Locale.getDefault())
            var addresses = gc.getFromLocationName(city,2)
            var address = addresses.get(0)
            textView2.visibility = View.VISIBLE
            textView2.setText("${address.latitude} \n ${address.longitude} \n ${address.locality}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            button.isEnabled = true

    }
}