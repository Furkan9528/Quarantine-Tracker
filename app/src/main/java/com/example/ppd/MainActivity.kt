package com.example.ppd

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    var distance: Int = 0;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }



        mainMenu.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("situation", situation.text.toString())
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            /*var intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
            finish()
            */

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
                    address.text=tasks.get("Address").toString()
                    emailLog.text=tasks.get("email").toString()
                    date_conge.text=tasks.get("Date").toString()
                    var city = tasks.get("Address").toString()
                    var gc = Geocoder(this, Locale.getDefault())
                    var addresses = gc.getFromLocationName(city,2)
                    var address = addresses.get(0)
                    latlng.visibility = View.VISIBLE
                    latlng.setText("${address.latitude} \n ${address.longitude} \n ${address.locality}")
                    fetchLocation(address.latitude, address.longitude)

                    val dateStr = "${date_conge.text}"
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    val formatter = SimpleDateFormat("dd/MM/yyyy")
                    val date = formatter.format(sdf.parse(dateStr).plusDays(14))
                    date_fin.setText("${date}")

                    //val now = date_conge.text
                    //val tomorrow = now.plusDays(6)
                    //  val sdf = SimpleDateFormat("dd/MM/yyyy")
                    //val date = sdf.parse(tomorrow.toString())


                }
        }
    }




     private fun distance(startLatitude:Double, startLongitude:Double, endLatitude: Double, endLongitude:Double ){

         //Calculate distance  //
        val results = FloatArray(2)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
        val distance = results[0]
        val kilometre = (distance / 1000).toInt()

        if(kilometre <2){
            situation.setText("Vous êtes chez vous !")
        }else{
            situation.setText("Vous n'êtes pas chez vous !")
        }

        locationadress.visibility = View.VISIBLE
        locationadress.setText("${kilometre} ${"Km"} ")


    }
}


private fun Date.plusDays(n: Int)= Date(this.time + n * 86400000)
