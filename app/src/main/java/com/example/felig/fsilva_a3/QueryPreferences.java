package com.example.felig.fsilva_a3;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by felig on 4/22/2018.
 */

public class QueryPreferences {
    private static final String PREF_LAST_RESULT_ID = "lastResultId";

    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_ID, null);
    }
    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

}
