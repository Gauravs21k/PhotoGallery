package com.gaurav.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private  static final String SEARCH_QUERY = "searchQuery";

    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SEARCH_QUERY, query)
                .apply();
    }
}
