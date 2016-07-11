package com.time_em.geofencing;

import java.util.HashMap;
import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.utils.Utils;
import org.json.JSONObject;


public class BackgroundLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "Bckgd_LocationService";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    public void onCreate() {


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5*60*1000); // Update location every 5 minute

        int permission_finelocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarse_location = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission_finelocation == PackageManager.PERMISSION_GRANTED
                || permission_coarse_location == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "" + location.getLatitude() + "," + "" + location.getLongitude();
        System.err.println("User current location=" + msg);

      //  Path : http://timeemapi.azurewebsites.net/api/Worksite/AddUsersTimeIn
      //  Type : Post
       // Parameter : UserId,points(latitude,longitude)

        HashMap<String, String> postDataParameters = new HashMap<String, String>();
        String userId=Utils.getSharedPrefs(getApplicationContext(),"apiUserId");
        postDataParameters.put("UserId", userId);
        postDataParameters.put("points", location.getLatitude() + "," + location.getLongitude());
        //postDataParameters.put("points", "19.225842" + "," + "72.9766845");
        Log.e(""+ Utils.AddUsersTimeIn,""+postDataParameters.toString());
        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm("post", Utils.AddUsersTimeIn, postDataParameters,"Update...");
        mWebPageTask.execute();

        //System.err.println("current location=" + msg);

    }


    public class AsyncTaskTimeEm extends AsyncTask<String, Void, String> {

       // private Activity activity;
        private Context context;
        public AsyncResponseTimeEm delegate = null;
        private String result = "";
        private ProgressDialog pDialog;
        private String methodName, message, method_type;
        private HashMap<String, String> postDataParameters;
        private JSONObject jsonObject = new JSONObject();


        public AsyncTaskTimeEm(String method_type, String methodName, HashMap<String, String> postDataParameters, String message) {
             this.method_type = method_type;
            this.methodName = methodName;
            this.postDataParameters = postDataParameters;
            this.message = message;
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
             if (method_type.equalsIgnoreCase("post") || method_type.equalsIgnoreCase("postJson")) {
//
                result = Utils.getResponseFromUrlPost(false, methodName, postDataParameters, getApplicationContext(), jsonObject, method_type);
            }

            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.e("result",""+result);

        }
    }
}
