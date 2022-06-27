package com.example.ppd

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

/**
* Hérite de BroadcastReveiver , attends un singnal de l'alarm manager pour lancer la notification modélisée.
*  La notification reçois ici un bout de code à utiliser quand on clique dessus , elle ouvre MapsActivity.
*/
class Notification : BroadcastReceiver()
{
    /**
    * Réagis au signal de l'alarme manager et modélise une notification à envoyer ainsi que son avtion de clique (ouvrir l'application sur MapsActivity)
    */
    override fun onReceive(context: Context, intent: Intent)
    {
        // Zone : Action de l'application , redirige vers MapsAvtivity
        val resultIntent = Intent(context, MapsActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        //Modélisation de la notification à afficher à partir de l'intent reçu par l'alarm Manager contenant le titre et la description
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setContentIntent(resultPendingIntent)
            .build()


        //Envoi de la notification
        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}
