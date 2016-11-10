package com.time_em.notifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.Notification;
import com.time_em.model.SpinnerData;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.tasks.AddEditTaskEntry;
import com.time_em.utils.FileUtils;
import com.time_em.utils.PrefUtils;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SendNotificationActivity extends Activity implements AsyncResponseTimeEm {

    //todo widgets
    private Spinner spnNotificationType;
    private EditText subject, message;
    private RelativeLayout recipients;
    private ImageView uploadedImage, back, rightNavigation, imgdelete, videodelete,dropdown1,dropdown2;
    private TextView txtSpnUsers, headerInfo,txt_Image_Video;
    private Button sendNotification;
    private ProgressDialog pDialog;
    private LinearLayout upload;
    //todo array list
    private ArrayList<User> userList=new ArrayList<>();
    private ArrayList<Notification> offline_notification=new ArrayList<>();
    private ArrayList<SpinnerData> notificationTypes;
    //todo classes
    private Time_emJsonParser parser;
    private SpinnerTypeAdapter adapter;
    private TimeEmDbHandler dbHandler;
   //todo variables
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    private byte[] multipartBody;
    private String sendNotificationAPI = Utils.SendNotificationAPI;
    private String selectedIds, selectedUsers;
    //userChoosenTask,selectedNotificationTypeId,attachmentPath,selectedNotificationTypeName;
    private FileUtils fileUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_notification);

        initScreen();
      //loadNotificationTypes();
        loadRecipients();
        setListeners();
        keyBoard_DoneButton();

    }

    private void initScreen() {
        Utils.saveInSharedPrefs(getApplicationContext(),"notification_purchase","yes");
        AddEditTaskEntry.UniqueNumber= FileUtils.getUniqueNumber(SendNotificationActivity.this);
        fileUtils = new FileUtils(SendNotificationActivity.this);
        dbHandler = new TimeEmDbHandler(SendNotificationActivity.this);
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
        parser = new Time_emJsonParser(SendNotificationActivity.this);

        imgdelete=(ImageView) findViewById(R.id.imgdelete);
        videodelete=(ImageView) findViewById(R.id.videodelete);
        imgdelete.setVisibility(View.GONE);
        videodelete.setVisibility(View.GONE);

        dropdown1=(ImageView) findViewById(R.id.dropdown1);
        dropdown2=(ImageView) findViewById(R.id.dropdown2);
        dropdown1.setVisibility(View.GONE);
        dropdown2.setVisibility(View.GONE);

        Utils.saveInSharedPrefs(SendNotificationActivity.this, "SelectedIds", "");
        Utils.saveInSharedPrefs(SendNotificationActivity.this, "SelectedUsers", "");

    }

    private void setListeners(){
        recipients.setOnClickListener(listener);
        upload.setOnClickListener(listener);
        sendNotification.setOnClickListener(listener);
        back.setOnClickListener(listener);
        imgdelete.setOnClickListener(listener);
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spnNotificationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                SpinnerData notType = adapter.getItem(position);
                // Here you can do the action you want to...
                // selectedNotificationTypeId = String.valueOf(notType.getId());
                //selectedNotificationTypeName=String.valueOf(notType.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        subject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subject.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v== sendNotification){
             //  Log.e("selectedNotiId",""+selectedNotificationTypeId);

                if(Utils.hasText(subject)){}
                if(Utils.hasText(message)){}


                if( selectedIds.equals("")){
                    //Utils.showToast(SendNotificationActivity.this, "Please select any recipient.");
                    Utils.alertMessageWithoutBack(SendNotificationActivity.this,"Please select any recipient.");
                }else if(!Utils.hasText(subject) || !Utils.hasText(message) ){
                    return;
                }
              /*  else if(selectedNotificationTypeId.equals("0"))
                {
                    Utils.showToast(SendNotificationActivity.this, "Please select specify notification type.");
                    }*/
                else {

               if(Utils.isNetworkAvailable(SendNotificationActivity.this)) {
                    String getSPrefsId = Utils.getSharedPrefs(getApplicationContext(),PrefUtils.KEY_USER_ID);
                    HashMap<String, String> postDataParameters = new HashMap<String, String>();
                    postDataParameters.put("UserId", getSPrefsId);
                    postDataParameters.put("Subject", subject.getText().toString());
                    postDataParameters.put("Message", message.getText().toString());
                    postDataParameters.put("NotificationTypeId", "0");
                    postDataParameters.put("notifyto", selectedIds);
                    postDataParameters.put("CompanyId", PrefUtils.getStringPreference(SendNotificationActivity.this,PrefUtils.KEY_COMPANY));

                    Log.e(""+Utils.AddNotificationNew, ""+postDataParameters.toString());

                    AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                            SendNotificationActivity.this, "post", Utils.AddNotificationNew,
                            postDataParameters, true, "Please wait...");
                    mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotificationActivity.this;
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
                        // Insert the string into db .
                   int getSPrefsId = Integer.parseInt(Utils.getSharedPrefs(getApplicationContext(),PrefUtils.KEY_USER_ID));
                        Notification notification = new Notification();
                        notification.setAttachmentPath(fileUtils.getAttachmentPath());
                        notification.setUserId(getSPrefsId);
                        notification.setSubject(subject.getText().toString());
                        notification.setMessage(message.getText().toString());
                        notification.setNotificationId(0);
                        notification.setNotificationTypeId(0);
                        notification.setSenderId(selectedIds);
                       // notification.setNotificationType(selectedNotificationTypeName);
                        notification.setCreatedDate(getCurrentDate());
                        notification.setSenderFullName(selectedUsers);
                        String companyId=PrefUtils.getStringPreference(SendNotificationActivity.this,PrefUtils.KEY_COMPANY);
                        notification.setCompanyId(companyId);

                        notification.setTimeZone(getCurrentDate());
                        notification.setIsOffline("true");
                        long timeStamp = System.currentTimeMillis();
                        notification.setUniqueNumber(getSPrefsId + "" + timeStamp);
                        Log.e("",""+notification.toString());
                        offline_notification.add(notification);
                        dbHandler.updateNotifications(offline_notification);
                        Utils.alertMessage(SendNotificationActivity.this,"Send Notification Successfully.!");
                         }

                }
            }else if(v == imgdelete){
                uploadedImage.setVisibility(View.GONE);
                imgdelete.setVisibility(View.GONE);
                fileUtils.setAttachmentPath(null);
            } else if(v == recipients){
                showUserSelectionDropdown();
            }else if(v == upload){
                fileUtils.showChooserDialog(false);
            }else if(v == back){
                finish();
            }
        }
    };



 /*   private void loadNotificationTypes() {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("CompanyId", PrefUtils.getStringPreference(SendNotificationActivity.this,PrefUtils.KEY_COMPANY));
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    SendNotificationActivity.this, "get", Utils.getNotificationType,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotificationActivity.this;
            mWebPageTask.execute();

         }
*/

    private void loadRecipients() {
        String getSPrefsId = Utils.getSharedPrefs(getApplicationContext(),PrefUtils.KEY_USER_ID);
        String getCompanyId=PrefUtils.getStringPreference(getApplicationContext(),PrefUtils.KEY_COMPANY);
           String timeStamp = Utils.getSharedPrefs(SendNotificationActivity.this, getSPrefsId+getCompanyId+ getResources().getString(R.string.activeUsersTimeStampStr));
            if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp = "";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("UserId", getSPrefsId);
            postDataParameters.put("timeStamp", "");
            postDataParameters.put("CompanyId", PrefUtils.getStringPreference(SendNotificationActivity.this,PrefUtils.KEY_COMPANY));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    SendNotificationActivity.this, "post", Utils.getActiveUserList,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) SendNotificationActivity.this;
            mWebPageTask.execute();
    }

    @Override
    public void processFinish(String output, String methodName) {

        if (methodName.equalsIgnoreCase(Utils.getActiveUserList)) {
            userList = parser.parseActiveUsers(output);

            if(userList!=null)
                dbHandler.updateActiveUsers(userList);

            String companyId=PrefUtils.getStringPreference(SendNotificationActivity.this,PrefUtils.KEY_COMPANY);
            userList = dbHandler.getActiveUsers(companyId);
        }
   /*     else if (methodName.equals(Utils.getNotificationType)) {
            notificationTypes = parser.parseNotificationType(output);
            dbHandler.updateNotificationType(notificationTypes);//update data for notification type

            notificationTypes=dbHandler.getNotificationTypeData();
            adapter = new SpinnerTypeAdapter(SendNotificationActivity.this,
                    R.layout.spinner_row_layout, notificationTypes);
            spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        }*/
        else if(methodName.equalsIgnoreCase(Utils.AddNotificationNew)){
            String Id = parser.getTaskId(output);
            if(Id.equalsIgnoreCase("0")){
                Utils.showToast(SendNotificationActivity.this,Utils.Api_error);
            }
            else {
                finish();
                if(fileUtils.getAttachmentPath() !=null) {
                    syncUploadFile(Id);
                }else{
                    Utils.showToast(getApplicationContext(), "Notification send successfully.");
                }
            }
        }
    }

    private void showUserSelectionDropdown(){
        if(userList!=null) {
            Intent intent = new Intent(SendNotificationActivity.this, UserSelectionActivity.class);
            intent.putExtra("activeUsers", userList);
            intent.putExtra("selectedIds", selectedIds);
            startActivity(intent);
        }else{
            Utils.showToast(getApplicationContext(),"No any user active.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedIds = Utils.getSharedPrefs(SendNotificationActivity.this, "SelectedIds");
        selectedUsers = Utils.getSharedPrefs(SendNotificationActivity.this, "SelectedUsers");
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
            if (requestCode == FileUtils.SELECT_FILE){
                fileUtils.onSelectFromGalleryResult(data, uploadedImage);
                imgdelete.setVisibility(View.VISIBLE); }
            else if (requestCode == FileUtils.REQUEST_CAMERA){
                fileUtils.onCaptureImageResult(data, uploadedImage);
                imgdelete.setVisibility(View.VISIBLE); }
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

        fileUtils.sendMultipartRequest("other",Utils.SyncFileUpload, dataModels);

    }
    private void  keyBoard_DoneButton()
    {
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideKeyboard(SendNotificationActivity.this);
                    return true;
                }
                return false;
            }
        });
    }

}