package com.gaurav.photogallery.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gaurav.photogallery.FlickrFetcher;
import com.gaurav.photogallery.GalleryItem;
import com.gaurav.photogallery.PhotoGalleryActivity;
import com.gaurav.photogallery.QueryPreferences;
import com.gaurav.photogallery.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {
   private static final String TAG = "PollService";
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent newIntent(Context context) {
       return new Intent(context, PollService.class);
   }
    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
       if(!isNetworkAvailableAndConnected()) {
           return;
       }

       String query = QueryPreferences.getStoredQuery(this);
       String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetcher().fetchRecentPhotos();
        } else {
            items = new FlickrFetcher().searchPhotos(query);
        }

        if (items.size() == 0) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification",
                    "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);

            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);

            Notification notification = new NotificationCompat.Builder(this, "My notification")
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
        }

        QueryPreferences.setLastResultID(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean networkAvailableAndConnected = (connectivityManager.getActiveNetwork() != null) &&
                connectivityManager.getActiveNetworkInfo().isConnected();

        return networkAvailableAndConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
       Intent intent = PollService.newIntent(context);
       PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
       AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

       if (isOn) {
           alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                   SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
       } else {
           alarmManager.cancel(pendingIntent);
           pendingIntent.cancel();
       }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context,
                0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }
}
