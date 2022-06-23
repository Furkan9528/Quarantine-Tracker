package com.example.ppd

import android.app.NotificationManager
import android.content.Context
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class CheckPositionWork (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    var channelId =  "channel1";
    var notificationId  = 1;

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

        } else {
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
    }


