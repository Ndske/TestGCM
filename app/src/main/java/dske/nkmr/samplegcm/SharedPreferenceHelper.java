package dske.nkmr.samplegcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferenceへの保存のためのヘルパークラス.
 */
public class SharedPreferenceHelper {

    private final String PREFERENCE_NAME = "gcm_sample";

    public boolean saveString(Context con, String key, String content) {
        if (con == null) {
            return false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.edit().putString(key, content).commit();
    }

    public String getString(Context con, String key) {
        if (con == null) {
            return null;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getString(key, null);
    }

    public boolean saveBool(Context con, String key, boolean content) {
        if (con == null) {
            return false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.edit().putBoolean(key, content).commit();
    }

    public boolean getBool(Context con, String key) {
        if (con == null) {
            return false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.getBoolean(key, false);
    }

    public boolean remove(Context con, String key){
        if (con == null) {
            return false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.edit().remove(key).commit();
    }

    public boolean clear(Context con){
        if(con == null){
            return false;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);
        return sp.edit().clear().commit();
    }
}
