package edu.gatech.spacebarz.buzzshelter.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores the local user's logon information
 */
public class LocalUser {

    private static final String USER_PREF_NAME = "SBBuzzShelterPrefs";
    private static final String USER_PREF_USERNAME = "SBBuzzShelterPrefsUsername";

    /** The global instance to be used by all facets of the application */
    private static LocalUser instance;

    /**
     * Gets the global LocalUser instance
     * @param context The current context (used to retrieve shared preferences information)
     * @return The instance of LocalUser to use throughout the app
     */
    public static LocalUser getInstance(Context context) {
        if (instance == null) {
            instance = new LocalUser(context);
        }
        return instance;
    }

    /** The username of the logged-in user */
    private String username;

    /** The context of the app used to store/retrieve preferences */
    private Context context;

    /** Generates a LocalUser instance from the values stored in SharedPreferences */
    private LocalUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREF_NAME, 0);
        this.username = settings.getString(USER_PREF_USERNAME, null);
        this.context = context;
    }

    /** Returns the logged-in user's username */
    public String getUsername() {
        return username;
    }

    /** Stores the login information of the user */
    public void login(String username) {
        this.username = username;
        SharedPreferences settings = context.getSharedPreferences(USER_PREF_NAME, 0);
        settings.edit().putString(USER_PREF_USERNAME, username).apply();
    }

    /** Removes the information of the logged-in user on logout */
    public void logout() {
        this.username = null;
        SharedPreferences settings = context.getSharedPreferences(USER_PREF_NAME, 0);
        settings.edit().putString(USER_PREF_USERNAME, null).apply();
    }

    /** Returns whether or not the local device is logged into an account */
    public boolean isLoggedIn() {
        return username != null;
    }

}
