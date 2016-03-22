package id.co.pln.simapro.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Rezpa Aditya on 3/17/2016.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // nama sharepreference
    private static final String PREF_NAME = "Sesi";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String ID_USER = "id_user";
    public static final String ID_UNIT_USER = "id_unit_user";
    public static final String NM_USER = "nm_user";
    public static final String STATUS_USER = "status_user";
    public static final String ROLE_USER = "role_user";
    public static final String EMAIL_USER = "email_user";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id_user,
                                   String id_unit_user,
                                   String nm_user,
                                   String status_user,
                                   String role_user,
                                   String email_user){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(ID_USER, id_user);
        editor.putString(ID_UNIT_USER, id_unit_user);
        editor.putString(NM_USER, nm_user);
        editor.putString(STATUS_USER, status_user);
        editor.putString(ROLE_USER, role_user);
        editor.putString(EMAIL_USER, email_user);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public boolean checkLogin(){
        if(!this.isLoggedIn()){
            return false;
        }
        return true;
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(ID_USER, pref.getString(ID_USER, null));
        user.put(ID_UNIT_USER, pref.getString(ID_UNIT_USER, null));
        user.put(NM_USER, pref.getString(NM_USER, null));
        user.put(STATUS_USER, pref.getString(STATUS_USER, null));
        user.put(ROLE_USER, pref.getString(ROLE_USER, null));
        user.put(EMAIL_USER, pref.getString(EMAIL_USER, null));
        return user;
    }

    public void changeStatus(String status){
        editor.putString(STATUS_USER, status);
        editor.commit();
    }

    /**
     * Clear session details
     * */
    public boolean logoutUser(){
        // Clearing all data from Shared Preferences
        //editor.clear();
        editor.remove(IS_LOGIN);
        editor.remove(ID_USER);
        editor.remove(ID_UNIT_USER);
        editor.remove(NM_USER);
        editor.remove(STATUS_USER);
        editor.remove(ROLE_USER);
        editor.remove(EMAIL_USER);
        editor.commit();
        return true;
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
