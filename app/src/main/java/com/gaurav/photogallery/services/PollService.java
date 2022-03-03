package com.gaurav.photogallery.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

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

    }
}
