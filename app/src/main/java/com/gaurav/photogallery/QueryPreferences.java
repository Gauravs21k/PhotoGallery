package com.gaurav.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private  static final String SEARCH_QUERY = "searchQuery";
    private static final String LAST_RESULT_ID = "lastResultId";

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

    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(LAST_RESULT_ID, null);
    }

    public static void setLastResultID(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(LAST_RESULT_ID, lastResultId)
                .apply();
    }
}
