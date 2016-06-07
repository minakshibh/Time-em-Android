package com.time_em.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.NotificationType;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.CustomMultipartRequest;
import com.time_em.utils.MultipartRequest;
import com.time_em.utils.MultipartUtility;
import com.time_em.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import com.time_em.android.R;
import com.time_em.utils.VolleySingleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class SendNotification extends Activity implements AsyncResponseTimeEm {

    private Spinner spnNotificationType;
    private EditText subject, message;
    private RelativeLayout recipients;
    private ArrayList<User> userList;
    private ArrayList<NotificationType> notificationTypes;
    private Time_emJsonParser parser;
    private NotificationTypeAdapter adapter;
    private String selectedIds, selectedUsers, userChoosenTask, selectedNotificationTypeId, attachmentPath;
    private ImageView uploadedImage;
    private int SELECT_FILE = 1, REQUEST_CAMERA = 2;
    private Button sendNotification;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private ProgressDialog pDialog;
    private LinearLayout upload;
    private TextView txtSpnUsers, headerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_notification);

        initScreen();
        loadNotificationTypes();
        loadRecipients();
    }

    private void initScreen() {
        spnNotificationType = (Spinner) findViewById(R.id.spnNotificationType);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        recipients = (RelativeLayout) findViewById(R.id.spnUsers);
        upload = (LinearLayout) findViewById(R.id.upload);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        sendNotification = (Button) findViewById(R.id.send);
        txtSpnUsers = (TextView)findViewById(R.id.txtSpnUsers);
        headerInfo = (TextView)findViewById(R.id.info);

        headerInfo.setText("Send Notification");
        parser = new Time_emJsonParser(SendNotification.this);

        Utils.saveInSharedPrefs(SendNotification.this, "SelectedIds", "");
        Utils.saveInSharedPrefs(SendNotification.this, "SelectedUsers", "");

        recipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserSelectionDropdown();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooserDialog();
            }
        });
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spnNotificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                NotificationType notType = adapter.getItem(position);
                // Here you can do the action you want to...
                selectedNotificationTypeId = String.valueOf(notType.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AsyncTaskNotification().execute();

                if(subject.getText().toString().trim().equals("") || message.getText().toString().trim().equals("")
                        || selectedNotificationTypeId.equals("") || selectedIds.equals("")){
                    Utils.showToast(SendNotification.this, "Please specify required information");
                }else {
                    sendNotification();
                }
            }
        });
    }

    private void showChooserDialog() {

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SendNotification.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utils.checkPermission(SendNotification.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        attachmentPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void loadNotificationTypes() {
        if (Utils.isNetworkAvailable(SendNotification.this)) {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    SendNotification.this, "get", Utils.getNotificationType,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotification.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(SendNotification.this, Utils.network_error);
        }
    }

    private void loadRecipients() {
        if (Utils.isNetworkAvailable(SendNotification.this)) {

            String timeStamp = Utils.getSharedPrefs(SendNotification.this, HomeActivity.user.getId() + getResources().getString(R.string.activeUsersTimeStampStr));
            if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp = "";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
            postDataParameters.put("timeStamp", timeStamp);

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    SendNotification.this, "post", Utils.getActiveUserList,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotification.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(SendNotification.this, Utils.network_error);
        }
    }

    public class NotificationTypeAdapter extends ArrayAdapter<NotificationType> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)

        public NotificationTypeAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.context = context;
        }

        public int getCount() {
            return notificationTypes.size();
        }

        public NotificationType getItem(int position) {
            return notificationTypes.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(notificationTypes.get(position).getName());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(notificationTypes.get(position).getName());

            return label;
        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        TimeEmDbHandler dbHandler = new TimeEmDbHandler(SendNotification.this);

        if (methodName.equals(Utils.getActiveUserList)) {
            userList = parser.parseActiveUsers(output);
            dbHandler.updateActiveUsers(userList);

            userList = dbHandler.getActiveUsers();
        }

        if (methodName.equals(Utils.getNotificationType)) {
            notificationTypes = parser.parseNotificationType(output);

            adapter = new NotificationTypeAdapter(SendNotification.this,
                    R.layout.spinner_row_layout);
            spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        }
    }

    private void showUserSelectionDropdown(){
        Intent intent = new Intent(SendNotification.this, UserSelectionActivity.class);

        intent.putExtra("activeUsers", userList);
        intent.putExtra("selectedIds", selectedIds);

        startActivity(intent);
    }

    private void sendNotification(){

        Log.e("Notification","Send notification called");

        pDialog = new ProgressDialog(SendNotification.this);
        pDialog.setTitle("Time'em");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = SendNotification.this.getResources().getString(R.string.baseUrl)+Utils.SendNotificationAPI;

        MultipartRequest mCustomRequest = new MultipartRequest(url, String.valueOf(HomeActivity.user.getId()),
                subject.getText().toString(), message.getText().toString(), selectedNotificationTypeId, selectedIds, attachmentPath, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("volley error", "::: error , "+error.getMessage());
                Utils.showToast(SendNotification.this, "Something went wrong, "+error.getMessage());
            }
        }, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("volley","uploaded successfully");

                try {
                    String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.e("volley response", ":::"+responseString);

                    pDialog.dismiss();
                    Utils.alertMessage(SendNotification.this, "::::: " + responseString);

                }catch (Exception e){
                    e.printStackTrace();
                    pDialog.dismiss();
                    Utils.showToast(SendNotification.this, "Something went wrong, "+e.getMessage());
                    Log.e("volley response", "::: error , "+e.getMessage());
                }
            }
        });

        mCustomRequest.setShouldCache(false);
        mCustomRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(SendNotification.this).addToRequestQueue(mCustomRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedIds = Utils.getSharedPrefs(SendNotification.this, "SelectedIds");
        selectedUsers = Utils.getSharedPrefs(SendNotification.this, "SelectedUsers");
        txtSpnUsers.setText(selectedUsers);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                //code for deny
                }
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        attachmentPath = cursor.getString(columnIndex);
        cursor.close();

        uploadedImage.setImageBitmap(getScaledBitmap(attachmentPath, 800, 800));

    }
    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    private void onCaptureImageResult(Intent data) {
        try {
            uploadedImage.setImageBitmap(getScaledBitmap(attachmentPath, 800, 800));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
}