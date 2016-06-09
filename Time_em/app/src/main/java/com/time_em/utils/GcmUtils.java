
package com.time_em.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


public class GcmUtils {

    private static final String TAG = "GcmUtils";

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";


	public interface RegisterListener {
		public void onRegistered(String regId);
		public void onError(Exception e);
	}
    public static void registerInBackground(final Context context, final RegisterListener listener) {
        AsyncTask<Void, Exception, String> task = new AsyncTask<Void, Exception, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String regId = gcm.register(Utils.PROJECT_ID);

                    storeRegistrationId(context, regId);
	                return regId;
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }

	        @Override
	        protected void onProgressUpdate(Exception... values) {
		        super.onProgressUpdate(values);
		        listener.onError(values[0]);
	        }

	        @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
		        if(s != null)
	                listener.onRegistered(s);
		        else
			        listener.onError(new Exception("something is wrong"));
            }
        };
        task.execute();
    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId(final Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
           Log.i(TAG, "Registration not found.");

            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {

             Log.i(TAG, "App version changed.");

            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGCMPreferences(final Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
