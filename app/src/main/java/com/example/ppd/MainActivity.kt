package com.example.ppd

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    val mapsActivity = MapsActivity()
    var distance: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        val findMap = findViewById<Button>(R.id.findMap)
        findMap.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("distance",this.distance)
            startActivity(intent)
        }

        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")
        logout.setOnClickListener {
            sharedPref.edit().remove("Email").apply()
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(isLogin=="1")
        {
            var email=intent.getStringExtra("email")
            if(email!=null)
            {
                setText(email)
                with(sharedPref.edit())
                {
                    putString("Email",email)
                    apply()
                }
            }
            else{
                var intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else
        {
            setText(isLogin)
        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                111)

    }

    private fun fetchLocation(startLatitude: Double , startLongitude: Double) {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if(it != null ){
                actualLocation.visibility = View.VISIBLE
                actualLocation.setText("${it.latitude} \n ${it.longitude}")

                distance(it.latitude, it.longitude, startLatitude, startLongitude)

            }
        }
    }


    private fun setText(email:String?)
    {
        db= FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener {
                        tasks->
                    name.text=tasks.get("Name").toString()
                    phone.text=tasks.get("Phone").toString()
                    address.text=tasks.get("Address").toString()
                    emailLog.text=tasks.get("email").toString()
                    var city = tasks.get("Address").toString()
                    var gc = Geocoder(this, Locale.getDefault())
                    var addresses = gc.getFromLocationName(city,2)
                    var address = addresses.get(0)
                    latlng.visibility = View.VISIBLE
                    latlng.setText("${address.latitude} \n ${address.longitude} \n ${address.locality}")
                    fetchLocation(address.latitude, address.longitude)
                }
        }
    }



     private fun distance(startLatitude:Double, startLongitude:Double, endLatitude: Double, endLongitude:Double ){

         //Calculate distance  //
        val results = FloatArray(2)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
        val distance = results[0]
        val kilometre = (distance / 1000).toInt()

        this.distance = kilometre



        locationadress.visibility = View.VISIBLE
        locationadress.setText("${kilometre} ${"Km"} ")


    }


}