package com.time_em.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.SpinnerData;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.FileUtils;
import com.time_em.utils.MultipartRequest;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;
import com.time_em.utils.VolleySingleton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SendNotification extends Activity implements AsyncResponseTimeEm {

    private Spinner spnNotificationType;
    private EditText subject, message;
    private RelativeLayout recipients;
    private ArrayList<User> userList;
    private ArrayList<SpinnerData> notificationTypes;
    private Time_emJsonParser parser;
    private SpinnerTypeAdapter adapter;
    private String selectedIds, selectedUsers, userChoosenTask, selectedNotificationTypeId, attachmentPath;
    private ImageView uploadedImage, back, rightNavigation;
    private Button sendNotification;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private ProgressDialog pDialog;
    private LinearLayout upload;
    private TextView txtSpnUsers, headerInfo;
    private FileUtils fileUtils;
    String sendNotificationAPI = Utils.SendNotificationAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_notification);

        initScreen();
        loadNotificationTypes();
        loadRecipients();
        setListeners();
    }

    private void initScreen() {
        fileUtils = new FileUtils(SendNotification.this);

        spnNotificationType = (Spinner) findViewById(R.id.spnNotificationType);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        recipients = (RelativeLayout) findViewById(R.id.spnUsers);
        upload = (LinearLayout) findViewById(R.id.upload);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        sendNotification = (Button) findViewById(R.id.send);
        txtSpnUsers = (TextView)findViewById(R.id.txtSpnUsers);
        headerInfo = (TextView)findViewById(R.id.headerText);
        back =(ImageView)findViewById(R.id.back);
        rightNavigation = (ImageView)findViewById(R.id.AddButton);
        rightNavigation.setVisibility(View.GONE);

        headerInfo.setText("Send Notification");
        parser = new Time_emJsonParser(SendNotification.this);

        Utils.saveInSharedPrefs(SendNotification.this, "SelectedIds", "");
        Utils.saveInSharedPrefs(SendNotification.this, "SelectedUsers", "");
    }

    private void setListeners(){
        recipients.setOnClickListener(listener);
        upload.setOnClickListener(listener);
        sendNotification.setOnClickListener(listener);
        back.setOnClickListener(listener);
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spnNotificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                SpinnerData notType = adapter.getItem(position);
                // Here you can do the action you want to...
                selectedNotificationTypeId = String.valueOf(notType.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v== sendNotification){
                if(subject.getText().toString().trim().equals("") || message.getText().toString().trim().equals("")
                        || selectedNotificationTypeId.equals("") || selectedIds.equals("")){
                    Utils.showToast(SendNotification.this, "Please specify required information");
                }else {
                    ArrayList<MultipartDataModel> dataModels = new ArrayList<>();

                    if(fileUtils.getAttachmentPath() !=null)
                        dataModels.add(new MultipartDataModel("profile_picture", fileUtils.getAttachmentPath(), MultipartDataModel.FILE_TYPE));

                    dataModels.add(new MultipartDataModel("UserId", String.valueOf(HomeActivity.user.getId()), MultipartDataModel.STRING_TYPE));
                    dataModels.add(new MultipartDataModel("Subject", subject.getText().toString(), MultipartDataModel.STRING_TYPE));
                    dataModels.add(new MultipartDataModel("Message", message.getText().toString(), MultipartDataModel.STRING_TYPE));
                    dataModels.add(new MultipartDataModel("NotificationTypeId", selectedNotificationTypeId, MultipartDataModel.STRING_TYPE));
                    dataModels.add(new MultipartDataModel("notifyto", selectedIds, MultipartDataModel.STRING_TYPE));

                    fileUtils.sendMultipartRequest(sendNotificationAPI, dataModels);
                }
            }else if(v == recipients){
                showUserSelectionDropdown();
            }else if(v == upload){
                fileUtils.showChooserDialog(false);
            }else if(v == back){
                finish();
            }
        }
    };

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

    @Override
    public void processFinish(String output, String methodName) {
        TimeEmDbHandler dbHandler = new TimeEmDbHandler(SendNotification.this);

        if (methodName.equals(Utils.getActiveUserList)) {
            userList = parser.parseActiveUsers(output);
            dbHandler.updateActiveUsers(userList);

            userList = dbHandler.getActiveUsers();
        }else if (methodName.equals(Utils.getNotificationType)) {
            notificationTypes = parser.parseNotificationType(output);

            adapter = new SpinnerTypeAdapter(SendNotification.this,
                    R.layout.spinner_row_layout, notificationTypes);
            spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        }
    }

    private void showUserSelectionDropdown(){
        Intent intent = new Intent(SendNotification.this, UserSelectionActivity.class);

        intent.putExtra("activeUsers", userList);
        intent.putExtra("selectedIds", selectedIds);

        startActivity(intent);
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
                    if(fileUtils.getUserChoosenTask().equals("Take Photo"))
                        fileUtils.cameraIntent();
                    else if(fileUtils.getUserChoosenTask().equals("Choose from Library"))
                        fileUtils.galleryIntent();
                } else {
                //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FileUtils.SELECT_FILE)
                fileUtils.onSelectFromGalleryResult(data, uploadedImage);
            else if (requestCode == FileUtils.REQUEST_CAMERA)
                fileUtils.onCaptureImageResult(data, uploadedImage);
        }
    }
}