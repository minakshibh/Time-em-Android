package com.time_em.notifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.Notification;
import com.time_em.model.SpinnerData;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.tasks.AddEditTaskEntry;
import com.time_em.utils.FileUtils;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SendNotification extends Activity implements AsyncResponseTimeEm {

    private Spinner spnNotificationType;
    private EditText subject, message;
    private RelativeLayout recipients;
    private ArrayList<User> userList;
    private ArrayList<Notification> offline_notification=new ArrayList<>();
    private ArrayList<SpinnerData> notificationTypes;
    private Time_emJsonParser parser;
    private SpinnerTypeAdapter adapter;
    private String selectedIds, selectedUsers, userChoosenTask, selectedNotificationTypeId, attachmentPath,selectedNotificationTypeName;
    private ImageView uploadedImage, back, rightNavigation;
    private Button sendNotification;
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private ProgressDialog pDialog;
    private LinearLayout upload;
    private TextView txtSpnUsers, headerInfo,txt_Image_Video;
    private FileUtils fileUtils;
    String sendNotificationAPI = Utils.SendNotificationAPI;
    TimeEmDbHandler dbHandler;
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
        AddEditTaskEntry.UniqueNumber= FileUtils.getUniqueNumber();
        fileUtils = new FileUtils(SendNotification.this);
        dbHandler = new TimeEmDbHandler(SendNotification.this);
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
        txt_Image_Video=(TextView)findViewById(R.id.txt_Image_Video);
        txt_Image_Video.setText("Upload Image");
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
                selectedNotificationTypeName=String.valueOf(notType.getName());
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
               Log.e("selectedNotiId",""+selectedNotificationTypeId);
                if(subject.getText().toString().trim().equals("") || message.getText().toString().trim().equals("")
                        || selectedNotificationTypeId.equals("") || selectedIds.equals("")){
                    Utils.showToast(SendNotification.this, "Please specify required information");
                }
                else if(selectedNotificationTypeId.equals("0"))
                {
                    Utils.showToast(SendNotification.this, "Please select specify notification type");
                    }

                else {



               if(Utils.isNetworkAvailable(SendNotification.this)) {
                    HashMap<String, String> postDataParameters = new HashMap<String, String>();

                    postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
                    postDataParameters.put("Subject", subject.getText().toString());
                    postDataParameters.put("Message", message.getText().toString());
                    postDataParameters.put("NotificationTypeId", selectedNotificationTypeId);
                    postDataParameters.put("notifyto", selectedIds);


                    Log.e(""+Utils.AddNotificationNew, ""+postDataParameters.toString());

                    AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                            SendNotification.this, "post", Utils.AddNotificationNew,
                            postDataParameters, true, "Please wait...");
                    mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotification.this;
                    mWebPageTask.execute();





                   /* ArrayList<MultipartDataModel> dataModels = new ArrayList<>();

                            if(fileUtils.getAttachmentPath() !=null)
                            dataModels.add(new MultipartDataModel("profile_picture", fileUtils.getAttachmentPath(), MultipartDataModel.FILE_TYPE));
                            dataModels.add(new MultipartDataModel("UserId", String.valueOf(HomeActivity.user.getId()), MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("Subject", subject.getText().toString(), MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("Message", message.getText().toString(), MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("NotificationTypeId", selectedNotificationTypeId, MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("notifyto", selectedIds, MultipartDataModel.STRING_TYPE));

                            fileUtils.sendMultipartRequest(sendNotificationAPI, dataModels);*/
                        }
                    else {
                        // Insert the string into db.

                        Notification notification = new Notification();
                        notification.setAttachmentPath(fileUtils.getAttachmentPath());
                        notification.setUserId(HomeActivity.user.getId());
                        notification.setSubject(subject.getText().toString());
                        notification.setMessage(message.getText().toString());
                        notification.setNotificationId(0);
                        notification.setNotificationTypeId(Integer.parseInt(selectedNotificationTypeId));
                        notification.setSenderId(Integer.parseInt(selectedIds));
                        notification.setNotificationType(selectedNotificationTypeName);
                        notification.setCreatedDate(getCurrentDate());
                        notification.setSenderFullName(selectedUsers);

                        notification.setTimeZone(getCurrentDate());
                        notification.setIsOffline("true");
                        long timeStamp = System.currentTimeMillis();
                        notification.setUniqueNumber(HomeActivity.user.getId() + "" + timeStamp);
                        Log.e("",""+notification.toString());
                        offline_notification.add(notification);
                        dbHandler.updateNotifications(offline_notification);
                        Utils.alertMessage(SendNotification.this,"Send Notification Successfully.!");
                         }

                }
            }
            else if(v == recipients){
                showUserSelectionDropdown();
            }else if(v == upload){
                fileUtils.showChooserDialog(false);
            }else if(v == back){
                finish();
            }
        }
    };



    private void loadNotificationTypes() {
       // if (Utils.isNetworkAvailable(SendNotification.this)) {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    SendNotification.this, "get", Utils.getNotificationType,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotification.this;
            mWebPageTask.execute();

        //} else {
         //   Utils.alertMessage(SendNotification.this, Utils.network_error);
       // }
    }

    private void loadRecipients() {
        //if (Utils.isNetworkAvailable(SendNotification.this)) {

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

       // } else {
           // Utils.alertMessage(SendNotification.this, Utils.network_error);
        //}
    }

    @Override
    public void processFinish(String output, String methodName) {


        if (methodName.equals(Utils.getActiveUserList)) {
            userList = parser.parseActiveUsers(output);
            dbHandler.updateActiveUsers(userList);

            userList = dbHandler.getActiveUsers();
        }else if (methodName.equals(Utils.getNotificationType)) {
            notificationTypes = parser.parseNotificationType(output);
            dbHandler.updateNotificationType(notificationTypes);//update data for notification type

            notificationTypes=dbHandler.getNotificationTypeData();
            adapter = new SpinnerTypeAdapter(SendNotification.this,
                    R.layout.spinner_row_layout, notificationTypes);
            spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        }
        else if(methodName.equalsIgnoreCase(Utils.AddNotificationNew)){
            String Id = parser.getTaskId(output);
            if(Id.equalsIgnoreCase("0")){
                Utils.showToast(SendNotification.this,Utils.Api_error);
            }
            else {
                finish();
                syncUploadFile(Id);
            }

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

    private String getCurrentDate()
    {
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String dateString = sdf.format(date);
        return dateString;
    }
    private void syncUploadFile(String Id) {

        String  ImagePath = fileUtils.getAttachmentPath();
        ArrayList<MultipartDataModel> dataModels = new ArrayList<>();
        dataModels.add(new MultipartDataModel("Id",Id, MultipartDataModel.STRING_TYPE));
        dataModels.add(new MultipartDataModel("FileUploadFor", "notification", MultipartDataModel.STRING_TYPE));

        if (ImagePath != null)
            dataModels.add(new MultipartDataModel("profile_picture", ImagePath, MultipartDataModel.FILE_TYPE));
        Log.e("send task", "send task" + ImagePath);

        fileUtils.sendMultipartRequest(Utils.SyncFileUpload, dataModels);
    }
}