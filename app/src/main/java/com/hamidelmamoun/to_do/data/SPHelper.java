package com.hamidelmamoun.to_do.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SPHelper {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final String NON_LOGGED_IN_USERS_ACCOUNT_ID = "abcdefghijklmnop";

    private static final String USER_FIRST_TIME = "user_first";
    private static final String LOGGED_IN_ACCOUNT_TYPE = "LOGGED_IN_ACCOUNT_TYPE";
    private static final String LOGGED_IN_ACCOUNT_ID = "LOGGED_IN_ACCOUNT_ID";
    private static final String SHOWCASE = "SHOWCASE";
    private static final String USER_FIRST_TASK = "USER_FIRST_TASK";

    public static void setUserFirstTime(Context context, boolean notFirstTime) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(USER_FIRST_TIME, notFirstTime);
        editor.apply();
    }

    public static boolean userFirstTime(Context context){
        return getPreferences(context).getBoolean(USER_FIRST_TIME, true);
    }

    public static void setAddedFirstTaskTrue(Context context, boolean notFirstTime) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(USER_FIRST_TASK, notFirstTime);
        editor.apply();
    }

    public static boolean addedFirstTask(Context context){
        return getPreferences(context).getBoolean(USER_FIRST_TASK, false);
    }

    /**
     * Set the Login Status
     * @param context
     * @param accountType
     */
    public static void setAccountType(Context context, int accountType) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(LOGGED_IN_ACCOUNT_TYPE, accountType);
        editor.apply();
    }

    public static int getAccountType(Context context){
        return getPreferences(context).getInt(LOGGED_IN_ACCOUNT_TYPE, 0);
    }

    /**
     * Set the Account ID: email
     * @param context
     * @param accountId
     */
    public static void setAccountId(Context context, String accountId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_ACCOUNT_ID, accountId);
        editor.apply();
    }

    public static String getAccountId(Context context){
        return getPreferences(context).getString(LOGGED_IN_ACCOUNT_ID, "");
    }

    public static boolean showcaseOver(Context context){
        return getPreferences(context).getBoolean(SHOWCASE,false);
    }

    public static void  setShowcaseOver(Context context){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(SHOWCASE, true);
        editor.apply();
    }

}
