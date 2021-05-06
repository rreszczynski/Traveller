package com.example.podroznik_s14983;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeoFenceReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "com.example.podroznik_s14983.NOTI_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Geofence> geoFences = GeofencingEvent.fromIntent(intent).getTriggeringGeofences();
        for(Geofence gf : geoFences) {
            NotificationManagerCompat notMan = NotificationManagerCompat.from(context);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Podróżnik - GeoFence", NotificationManager.IMPORTANCE_HIGH);
                notMan.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notCompatBuild = new NotificationCompat.Builder(context, CHANNEL_ID);
            notCompatBuild.setSmallIcon(R.drawable.ic_notif_icon);
            notCompatBuild.setContentTitle("Jesteś w znanym miejscu!");
            notCompatBuild.setContentText(gf.getRequestId());
            notCompatBuild.setAutoCancel(true);
            notCompatBuild.setPriority(NotificationManagerCompat.IMPORTANCE_HIGH);

            Notification noti = notCompatBuild.build();

            notMan.notify(1, noti);

            //Toast.makeText(context, "Jesteś w: " + gf.getRequestId(), Toast.LENGTH_SHORT).show();
        }
    }
}
