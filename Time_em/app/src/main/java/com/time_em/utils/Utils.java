package com.time_em.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.authentication.ChangeStatusActivity;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.SpinnerData;
import com.time_em.model.User;
import com.time_em.model.Widget;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

public class Utils {

    static public String network_error = "Please check your internet connection, try again.";
    static public String Api_error = "Something went wrong,please try again.";
    static int statusCode;
    static int connectTimeout=2*10*1000;
    static int socketTimeout=3*10*1000;
    public static final String Loation = "loation";
    private static SharedPreferences preferences;
    public static String PROJECT_ID = "554809123006";
    public static String API_ANDROID_KEY = "AIzaSyBGO_uGOOR6YyW0qvENCw_vGX6BHQY--RA";
    public static String API_SEVER_KEY="AIzaSyBUAfBKPEsEBsUzxKDPnk_w5WEY7bXEdDs";
    public static String sharedPrefs = "Time'emPrefs";

    static public String loginAPI = "/User/GetValidateUser";
    static public String tokenRegistrationAPI = "/Initiate/GetClientURL";
    static public String signInAPI = "/UserActivity/SignInByLoginId";
    static public String sigOutAPI = "/UserActivity/SignOutByLoginId";
    static public String pinAuthenticationAPI = "/User/GetValidateUserByPin";
    static public String getTeamAPI = "/User/GetAllUsersList";
    static public String GetUserActivityTask = "/UserTask/GetUserActivityTask";
    static public String forgotPasswordAPI = "/USER/ForgetPassword";

    static public String forgotPinAPI = "/USER/ForgetPin";
    static public String getSpinnerTypeAPI = "/Task/GetAssignedTaskIList";
    static public String AddUpdateUserTaskAPI = "/UserTask/AddUpdateUserTaskActivity";
    static public String deleteTaskAPI = "/UserTask/DeleteTask";
    static public String getNotificationType = "/Notification/GetNotificationType";
    static public String getActiveUserList = "/User/GetActiveUserList";
    static public String GetUsersListByLoginCode = "/User/GetUsersListByLoginCode";
    static public String SignInByUserId =  "/UserActivity/SignInByUserId";
    static public String SignOutByUserId =  "/UserActivity/SignOutByUserId";
    static public String SendNotificationAPI = "/Notification/AddNotification";
    static public String GetNotificationAPI = "/notification/NotificationByUserId";
    static public String RegisterUserDevice = "/User/RegisterUserDevice";
    static public String GetAssignedTaskIList = "/Task/GetAssignedTaskIList";
    static public String UserTaskGraph = "/usertask/UserTaskGraph";
    static public String UsersGraph = "/usertask/UsersGraph";
    static public String Sync = "/UserActivity/Sync";
    static public String SyncFileUpload = "/UserActivity/SyncFileUpload";
    static public String GetUsersListByTagId = "/User/GetUsersListByTagId";
    static public String GetUserWorksiteActivity ="/Worksite/GetUserWorksiteActivity";
    static public String GetUserListWorksiteActivity ="/worksite/GetUserlistWorksiteActivity";
    static public String AddUsersTimeIn="/Worksite/AddUsersTimeIn";
    static public String AddNotificationNew="notification/AddNotificationNew";
    static public String AddUpdateUserTaskActivityNew="usertask/AddUpdateUserTaskActivityNew";
    static public String GetUserCompaniesList="User/GetUserCompaniesList";

    //todo get all data at home page
    static public String GetAllTaskList="/UserTask/GetAllTaskList";
    static public String GetAllUsersListOffline = "/User/GetAllUsersListOffline";
    static public String GetAllNotificationByUserId ="/notification/GetAllNotificationByUserId";
    static public String GetActiveUserListoffline ="User/GetActiveUserListoffline";
    static public String GetAllAssignedTaskIList="task/GetAllAssignedTaskIList";


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary.");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void saveInSharedPrefs(Context context, String key, String value) {
        preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

   public static String getSharedPrefs(Context context, String key) {
        preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        String value = preferences.getString(key, "");
        return value;
    }

    public static void clearPreferences(Context context) {
        preferences = context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void alertMessage(final Context context, String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("");
        alert.setMessage(str);
        alert.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                ((Activity) context).finish();
            }


        });
        alert.show();
    }
    public static void alertMessageWithoutBack(final Context context, String str) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("");
        alert.setMessage(str);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getResponseFromUrlPost(Boolean token, String functionName, HashMap<String, String> postDataParams, Context context, JSONObject jsonObject, String methodType) {
        String requestString = context.getResources().getString(R.string.baseUrl) + "/" + functionName;
                Log.e("url",""+requestString);
        URL url;
        String response = "";
        try {
            url = new URL(requestString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(socketTimeout);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            if(methodType.equals("post"))
                writer.write(getPostDataString(postDataParams));
            else
                writer.write(jsonObject.toString());

            writer.flush();
            writer.close();
            os.close();
            statusCode = conn.getResponseCode();

            if (statusCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                    Log.e(functionName,response);
                }
            } else {
                response = "";
            }
        }
        catch (SocketException e) {
//            Toast.makeText(context,"Poor network, Try again.",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private static String getPostDataString(HashMap<String, String> params) {

        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static String getGetDataString(HashMap<String, String> params) {

        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                    result.append("?");
                } else
                    result.append("&");

//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String getResponseFromUrlGet(Boolean token, String functionName, HashMap<String, String> postDataParams, Context context) {

        String requestString = context.getResources().getString(R.string.baseUrl) + functionName;

        requestString += getGetDataString(postDataParams);
        Log.e("url", requestString);
        String response = "";

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(requestString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(socketTimeout);

            statusCode = urlConnection.getResponseCode();

            if (statusCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                    Log.e(functionName,response);
                }
            } else {
                response = "";
            }
        }
        catch (SocketException e) {
          // Toast.makeText(context,"Poor network, Try again.",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    public static void ChangeStatus(Context context,String userIds,String status) {


        if (Utils.isNetworkAvailable(context)) {
           String methodName="";

      //      postDataParameters.put("LoginId", user.getLoginID());

          //  Log.e("values", "login Id: " + user.getLoginID() + " ,user Id: " + user.getId() + " ,activity id: " + String.valueOf(user.getActivityId()));

           /* if (user.isSignedIn()) {
                apiMethod = Utils.sigOutAPI;
                postDataParameters.put("ActivityId", String.valueOf(user.getActivityId()));
            } else {
                apiMethod = Utils.signInAPI;
            }*/
            if(status.equalsIgnoreCase("signIn")) {
                methodName=Utils.SignInByUserId;
            }
            else if(status.equalsIgnoreCase("signOut")) {
                methodName=Utils.SignOutByUserId;
            }

            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("Userids", userIds);
            postDataParameters.put("CompanyId", PrefUtils.getStringPreference(context,PrefUtils.KEY_COMPANY));
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    (Activity) context, "get",  methodName,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) context;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(context, Utils.network_error);
        }
    }

    public static int resultCode() {
        return statusCode;
    }

    public static String getCurrentDate()
    {
        String str_date="";
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM, yyyy");
        str_date  = sdf.format(date);

        return str_date;
    }
    public static String formatDateChange(String date,String givenformat,String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(date));
        }
        catch(Exception e) {
            e.printStackTrace();
            return "";
        }
        finally {
            sdf=null;
            sdf1=null;
        }
        return result;
    }
    public static void hideKeyboard(Context cxt) {
        //   context=cxt;
        InputMethodManager inputManager = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = ((Activity) cxt).getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    // This four methods are used for maintaining widget
    public static void saveLocations(Context context,
                                     List<Widget> locations) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(sharedPrefs,Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(locations);
        editor.putString(Loation, jsonFavorites);
        editor.commit();
    }

    public static void addValues(Context context, Widget location) {
        List<Widget> favorites = getWidget(context);
        if (favorites == null)
            favorites = new ArrayList<Widget>();
        favorites.add(location);
        saveLocations(context, favorites);
    }

    public static void removeValues(Context context, int position) {
        ArrayList<Widget> loations = getWidget(context);
        if (loations != null) {
            // loations.remove(position ,loation);
            loations.remove(position);
            saveLocations(context, loations);
        }
    }

    public static ArrayList<Widget> getWidget(Context context) {
        SharedPreferences settings;
        List<Widget> loations;

        settings = context.getSharedPreferences(sharedPrefs,
                Context.MODE_PRIVATE);

        if (settings.contains(Loation)) {
            String jsonFavorites = settings.getString(Loation, null);
            Gson gson = new Gson();
            Widget[] favoriteItems = gson.fromJson(jsonFavorites,
                    Widget[].class);

            loations = Arrays.asList(favoriteItems);
            loations = new ArrayList<Widget>(loations);
        } else
            return null;

        return (ArrayList<Widget>) loations;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Required");
            return false;
        }

        return true;
    }

    public static String convertDate(Date dateFrom) throws ParseException {
        //String pattern = "yyyy/MM/dd HH:mm:ss";
        String pattern = "dd/MM/yyyy hh:mm";

        SimpleDateFormat sdfFrom = new SimpleDateFormat (pattern);
        sdfFrom.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat sdfTo = new SimpleDateFormat (pattern);
        sdfTo.setTimeZone(TimeZone.getDefault());

        Date dateTo = sdfFrom.parse(sdfTo.format(dateFrom));
        String timeTo = sdfTo.format(dateTo);
        return timeTo;
    }

    public static Date convertDate1(Date dateFrom) throws ParseException {
        //String pattern = "yyyy/MM/dd HH:mm:ss";
        String pattern = "dd/MM/yyyy hh:mm";

        SimpleDateFormat sdfFrom = new SimpleDateFormat (pattern);
        sdfFrom.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat sdfTo = new SimpleDateFormat (pattern);
        sdfTo.setTimeZone(TimeZone.getDefault());

        Date dateTo = sdfFrom.parse(sdfTo.format(dateFrom));
        String timeTo = sdfTo.format(dateTo);
        return dateTo;
    }
    
}
