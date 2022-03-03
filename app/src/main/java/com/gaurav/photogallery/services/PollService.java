package com.gaurav.photogallery.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gaurav.photogallery.FlickrFetcher;
import com.gaurav.photogallery.GalleryItem;
import com.gaurav.photogallery.QueryPreferences;

import java.util.List;

public class PollService extends IntentService {
   private static final String TAG = "PollService";

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

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
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
}
