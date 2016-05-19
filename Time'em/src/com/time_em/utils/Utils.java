package com.time_em.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.authentication.ChangeStatusActivity;
import com.time_em.dashboard.HomeActivity;

public class Utils {

static	public String network_error="Please check your internet connection, try again";
static int statusCode;
private static SharedPreferences preferences;

public static String sharedPrefs = "Time'emPrefs";

static public String loginAPI = "/User/GetValidateUser";
static public String tokenRegistrationAPI = "/Initiate/GetClientURL";
static public String signInAPI = "/UserActivity/SignInByLoginId";
static public String sigOutAPI = "/UserActivity/SignOutByLoginId";
static public String pinAuthenticationAPI = "/User/GetValidateUserByPin";
static public String getTeamAPI = "/User/GetAllUsersList";
static public String getTaskListAPI = "/UserTask/GetUserActivityTask";

	public static void showToast(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	public static void saveInSharedPrefs(Context context, String key, String value){
		preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getSharedPrefs(Context context, String key){
		preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
		String value = preferences.getString(key, "");
		return value;
	}
	
	public static void clearPreferences(Context context){
		preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void alertMessage(final Context context,String str)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("");
		alert.setMessage(str);
		alert.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			//((Activity) context).finish();
			}

		
		});
		alert.show();
	}
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public static String getResponseFromUrlPost(Boolean token,String functionName, HashMap<String, String> postDataParams, Context context){
		String requestString = context.getResources().getString(R.string.baseUrl)+"/"+functionName;
		
		URL url;
        String response = "";
        try {
            url = new URL(requestString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            statusCode=conn.getResponseCode();

            if (statusCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
	}
	
	private static String getPostDataString(HashMap<String, String> params) {

        StringBuilder result = new StringBuilder();
		try{
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first){
                first = false;
            }else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
		}catch(Exception e){
			e.printStackTrace();
		}
        return result.toString();
    }
	
	private static String getGetDataString(HashMap<String, String> params) {

        StringBuilder result = new StringBuilder();
		try{
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first){
                first = false;
                result.append("?");
            }else
                result.append("&");

//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
		}catch(Exception e){
			e.printStackTrace();
		}
        return result.toString();
    }
	
	public static String getResponseFromUrlGet(Boolean token,String functionName, HashMap<String, String> postDataParams, Context context){
			String requestString = context.getResources().getString(R.string.baseUrl)+functionName;
				
			requestString+=getGetDataString(postDataParams);
			Log.e("url", requestString);
			String response = "";
			
			URL url;
		    HttpURLConnection urlConnection = null;
		    try {
		        url = new URL(requestString);

		        urlConnection = (HttpURLConnection) url
		                .openConnection();

		        statusCode=urlConnection.getResponseCode();

	            if (statusCode == HttpsURLConnection.HTTP_OK) {
	                String line;
	                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	                while ((line=br.readLine()) != null) {
	                    response+=line;
	                }
	            }
	            else {
	                response="";
	            }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        if (urlConnection != null) {
		            urlConnection.disconnect();
		        }    
		    }
		    
				return response;
	}
	public static void ChangeStatus(Context context){
		String apiMethod="";
		
		if (Utils.isNetworkAvailable(context)) {

			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			postDataParameters.put("userId", String.valueOf(HomeActivity.user.getId()));
			postDataParameters.put("LoginId", HomeActivity.user.getLoginID());
			
			Log.e("values","login Id: "+HomeActivity.user.getLoginID()+" ,user Id: "+HomeActivity.user.getId()+" ,activity id: "+String.valueOf(HomeActivity.user.getActivityId()));
			
			if(HomeActivity.user.isSignedIn()){
				apiMethod = Utils.sigOutAPI;
				postDataParameters.put("ActivityId", String.valueOf(HomeActivity.user.getActivityId()));
			}else{
				apiMethod = Utils.signInAPI;
			}
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					(Activity) context, "post", apiMethod,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) context;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(context, Utils.network_error);
		}
	}

	public static int resultCode()
	{
			
		return statusCode;
		
	}
}
