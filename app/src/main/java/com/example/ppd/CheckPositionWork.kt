package com.example.ppd

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class CheckPositionWork (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    val channelId =  "channel1"
    val notificationId  = 1
    val db= FirebaseFirestore.getInstance()
    var LD_lat : Double =  0.0
    var LD_lon : Double = 0.0
    var AC_lat : Double =  0.0
    var AC_lon : Double = 0.0

    var lm = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gps_enabled = false
    var network_enabled = false

    override fun doWork(): Result {

        //Verifier la connexion internet puis la position
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
           }

        //si OK suivre sinon envoyer notif pour que le user le fasse lui meme
        if (gps_enabled && network_enabled) {

            getActualLocation()
            getLockDownLocation()

            //l'utilisateur est loins de chez lui signaler l'incident
            if(!isUserHome()){

            }

        }
        else {
            val notif = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Demande de Position")
                .setContentText("Veuillez renseigner votre position")
                .build()

            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationId, notif)


        }

        //Verifier la position de l'utilisateur puis la comparer avec celle du confinement
        //si OK ne rien faire sinon envoyer notif vs etes loins de chez vous signaler
        //ne pas notifier l'utilisateur la nuit mais poursuivre le work quand meme (le notifier s'il est lons de chez lui :p)


        // Indicate whether the work finished successfully with the Result
        return Result.retry()
    }

    //recupere la position du confinement depuis firebase
    private fun getLockDownLocation()
    {
        val email =  FirebaseAuth.getInstance().currentUser?.email.toString()
        if (email != null) {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener {
                        tasks->
                    var city = tasks.get("Address").toString()
                    var gc = Geocoder(applicationContext, Locale.getDefault())
                    var addresses = gc.getFromLocationName(city,2)
                    var address = addresses.get(0)
                    this.LD_lon  =  address.longitude
                    this.LD_lat = address.latitude
                }
        }
    }

    //recupere la position actuelle
    private fun getActualLocation(){
        val task: Task<Location> = LocationServices.getFusedLocationProviderClient(applicationContext).lastLocation
        if(ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                Activity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if(it != null ){
                this.AC_lon = it.longitude
                this.AC_lat  = it.latitude
            }
        }
    }

    private fun  isUserHome() : Boolean{
        val results = FloatArray(2)
        Location.distanceBetween(AC_lat, AC_lon, LD_lat, LD_lon, results)
        val distance = results[0]
        val metre = (distance / 1000000).toInt()

        return metre > 50
    }

    }


