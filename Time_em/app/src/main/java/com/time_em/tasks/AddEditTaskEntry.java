package com.time_em.tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.time_em.ImageLoader.ImageLoader;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.SpinnerData;
import com.time_em.model.TaskEntry;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.FileUtils;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class AddEditTaskEntry extends Activity implements AsyncResponseTimeEm{

    private TextView txtProjectSelection, txtCommentsHeader, txtHoursHeader, headerInfo, dateHeader;
    private Spinner spnProject;
    private ImageView hoursIcon, uploadedImage, back, rightNavigation;
    private LinearLayout recipientSection, uploadAttachment;
    private Button addUpdateTask;
    private EditText hours, comments;
    private ArrayList<SpinnerData> assignedTasks;
    private Time_emJsonParser parser;
    private SpinnerTypeAdapter adapter;
    private SpinnerData selectedSpinnerData;
    private FileUtils fileUtils;
    private String addUpdateTaskAPI = Utils.AddUpdateUserTaskAPI, selectedDate, taskEntryId = "0";
    private TaskEntry taskEntry;
    private VideoView uploadedVideo;
    TimeEmDbHandler dbHandler ;
    ArrayList<TaskEntry> taskEntries=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_notification);

        initScreen();
        loadProjects();
        setListeners();
    }

    private void initScreen() {
        parser = new Time_emJsonParser(AddEditTaskEntry.this);
        fileUtils = new FileUtils(AddEditTaskEntry.this);
        dbHandler = new TimeEmDbHandler(AddEditTaskEntry.this);

        txtProjectSelection = (TextView) findViewById(R.id.notificationTxt);
        txtCommentsHeader = (TextView) findViewById(R.id.SubjectTxt);
        comments = (EditText) findViewById(R.id.subject);
        txtHoursHeader = (TextView) findViewById(R.id.MessageTxt);
        hours = (EditText) findViewById(R.id.message);
        spnProject = (Spinner) findViewById(R.id.spnNotificationType);
        hoursIcon = (ImageView) findViewById(R.id.messageIcon);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        uploadedVideo=(VideoView)findViewById(R.id.uploadedVideo);
        recipientSection =(LinearLayout)findViewById(R.id.recipientSection);
        uploadAttachment = (LinearLayout)findViewById(R.id.upload);
        addUpdateTask = (Button)findViewById(R.id.send);
        headerInfo = (TextView)findViewById(R.id.headerText);
        back =(ImageView)findViewById(R.id.back);
        rightNavigation = (ImageView)findViewById(R.id.AddButton);
        rightNavigation.setVisibility(View.GONE);
        dateHeader = (TextView)findViewById(R.id.dateHeader);
        try{
        selectedDate = getIntent().getStringExtra("selectDate");
        taskEntry = getIntent().getParcelableExtra("taskEntry");
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        dateHeader.setVisibility(View.VISIBLE);
        dateHeader.setText(selectedDate);
        txtProjectSelection.setText("Select Project or Task");
        txtCommentsHeader.setText("Enter your Comments");
        comments.setHint("Your comments goes here");
        txtHoursHeader.setText("Specify no. of hours");
        hours.setHint("No. of hours");
        hours.setInputType(InputType.TYPE_CLASS_NUMBER);
        hoursIcon.setImageResource(R.drawable.user_icon);
        recipientSection.setVisibility(View.GONE);
        addUpdateTask.setText("Add Task Entry");
        assignedTasks = new ArrayList<>();

        if(taskEntry == null) {
            headerInfo.setText("Add Task Details");
        }else{
            headerInfo.setText("Edit Task Details");
            taskEntryId = String.valueOf(taskEntry.getId());
            comments.setText(taskEntry.getComments());

            hours.setText(String.valueOf(taskEntry.getTimeSpent()));
            String image_url = taskEntry.getAttachmentImageFile();
            int loader = R.drawable.add;
            // ImageLoader class instance
            if (image_url != null &&!image_url.equalsIgnoreCase("null")) {
                if(image_url.contains("http")) {
                    ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                    imgLoader.DisplayImage(image_url, loader, uploadedImage);
                }else{
                    FileUtils fileUtils=new FileUtils(AddEditTaskEntry.this);
                    uploadedImage.setImageBitmap(fileUtils.getScaledBitmap(image_url, 500, 500));
                }
            }
        }
    }

    private void selectedSpinnerValue(Spinner sp) {

        if(assignedTasks!=null && assignedTasks.equals("null")){
            for(int i=0;i<assignedTasks.size();i++)
            {
               String value=""+assignedTasks.get(i).getId();
                    if(taskEntryId.equalsIgnoreCase(value)) {
                        sp.setSelection(i);
                       // return;
                    }
                }
        }
    }

    private void setListeners(){
        uploadAttachment.setOnClickListener(listener);
        addUpdateTask.setOnClickListener(listener);
        back.setOnClickListener(listener);
        // You can create an anonymous listener to handle the event when is selected an spinner item
        spnProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                selectedSpinnerData = adapter.getItem(position);
                // Here you can do the action you want to...
//                selectedProjectId = String.valueOf(project.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v == back){
                finish();
            }else if(v == uploadAttachment){
                fileUtils.showChooserDialog(true);
            }else if(v == addUpdateTask){


                if(comments.getText().toString().trim().equals("") || hours.getText().toString().trim().equals("")
                        || selectedSpinnerData == null){
                    Utils.showToast(AddEditTaskEntry.this, "Please specify required information");
                }
                else if(selectedSpinnerData.getId()==0){
                    Utils.showToast(AddEditTaskEntry.this, "Please select specify project/task");
                }
              else {

                      Double intHours=Double.parseDouble(hours.getText().toString().trim());
                    if(intHours<=24) {
                        ArrayList<MultipartDataModel> dataModels = new ArrayList<>();
                   /* ["ActivityId": "29644", "CreatedDate": "06-10-2016", "UserId": "8049", "TaskId": "16168", "ID": "0",
                            "TaskName": "Test Task May 3", "TimeSpent": "10", "Comments": "Test Parv 10 June"]*/
                        if (fileUtils.getAttachmentPath() != null) {
                            if (fileUtils.getAttachmentPath().contains(".3gp")) {
                                dataModels.add(new MultipartDataModel("profile_video", fileUtils.getAttachmentPath(), MultipartDataModel.FILE_TYPE));
                            } else {
                                dataModels.add(new MultipartDataModel("profile_picture", fileUtils.getAttachmentPath(), MultipartDataModel.FILE_TYPE));
                            }
                        }
                        dataModels.add(new MultipartDataModel("UserId", String.valueOf(HomeActivity.user.getId()), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("ActivityId", String.valueOf(HomeActivity.user.getActivityId()), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("TimeSpent", hours.getText().toString(), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("Comments", comments.getText().toString(), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("TaskId", String.valueOf(selectedSpinnerData.getId()), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("TaskName", String.valueOf(selectedSpinnerData.getName()), MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("CreatedDate", selectedDate, MultipartDataModel.STRING_TYPE));
                        dataModels.add(new MultipartDataModel("ID", taskEntryId, MultipartDataModel.STRING_TYPE));

                        if (Utils.isNetworkAvailable(AddEditTaskEntry.this)) {
                            fileUtils.sendMultipartRequest(addUpdateTaskAPI, dataModels);
                        } else {
                            if (HomeActivity.user.isSignedIn()) {

                                long timeStamp = System.currentTimeMillis();
                                TaskEntry task = new TaskEntry();
                                if (fileUtils.getAttachmentPath() != null)
                                    task.setAttachmentImageFile(fileUtils.getAttachmentPath());
                                task.setId(Integer.parseInt(taskEntryId));
                                task.setActivityId(HomeActivity.user.getActivityId());
                                task.setTaskId(selectedSpinnerData.getId());
                                task.setUserId(HomeActivity.user.getId());
                                task.setTaskName(selectedSpinnerData.getName());
                                task.setTimeSpent(Double.parseDouble(hours.getText().toString()));
                                task.setComments(comments.getText().toString());
                                task.setSignedInHours(0.0);
                                task.setSelectedDate(selectedDate);
                                task.setIsActive(true);
                                task.setAttachmentImageFile(fileUtils.getAttachmentPath());
                                task.setIsoffline("true");
                                // task.setEndTime(taskObject.getString("EndTime"));
                                // task.setStartTime(taskObject.getString("StartTime"));

                                if (taskEntry == null) {   //for add new offline

                                    // task.setStartTime(taskObject.getString("StartTime"));
                                    Log.e("milliSecond", "" + timeStamp);
                                    task.setCreatedDate(HomeActivity.user.getId() + "" + timeStamp);
                                    Log.e("milliSecond +id", "" + HomeActivity.user.getId() + "" + timeStamp);

                                    task.setToken("00");
                                    taskEntries.add(task);
                                    dbHandler.updateTask(taskEntries, selectedDate, true);
                                    Utils.alertMessage(AddEditTaskEntry.this, "Task Add Successfully.!");
                                } //for edit delete old offline
                                else {


                                    Log.e("milliSecond", "" + timeStamp);
                                    task.setCreatedDate(taskEntry.getCreatedDate());
                                    Log.e("milliSecond +id", "" + HomeActivity.user.getId() + "" + timeStamp);

                                    task.setToken(taskEntry.getCreatedDate());
                                    taskEntries.add(task);
                                    dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                                    Utils.alertMessage(AddEditTaskEntry.this, "Task Updated Successfully.!");
                                }

                            }
                        else{
                                Utils.alertMessage(AddEditTaskEntry.this, "User is sign out, Please sign in first");
                        }
                        }
                    }else{
                        Utils.showToast(AddEditTaskEntry.this, "Please enter hours values less than 24");
                    }
                }
            }
        }
    };

    private void loadProjects() {
       // if (Utils.isNetworkAvailable(AddEditTaskEntry.this)) {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("userId", String.valueOf(HomeActivity.user.getId()));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    AddEditTaskEntry.this, "get", Utils.GetAssignedTaskList,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) AddEditTaskEntry.this;
            mWebPageTask.execute();

       // } else {
     //       Utils.alertMessage(AddEditTaskEntry.this, Utils.network_error);
      //  }
    }

    @Override
    public void processFinish(String output, String methodName) {
        if(methodName.equals(Utils.GetAssignedTaskList)){
            assignedTasks = parser.parseAssignedProjects(output);

            dbHandler.updateProjectTasks(assignedTasks);//update data for notification type

            assignedTasks=dbHandler.getProjectTasksData();
            adapter = new SpinnerTypeAdapter(AddEditTaskEntry.this,
                    R.layout.spinner_row_layout, assignedTasks);
            spnProject.setAdapter(adapter);
            if(taskEntry!=null)
            {
                selectedSpinnerValue(spnProject);
                }
        }
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
                    else if(fileUtils.getUserChoosenTask().equals("Record Video"))
                        fileUtils.videoIntent();
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
            if (requestCode == FileUtils.SELECT_FILE) {
                fileUtils.onSelectFromGalleryResult(data, uploadedImage);
                uploadedVideo.setVisibility(View.GONE);
                uploadedImage.setVisibility(View.VISIBLE);
            }
            else if (requestCode == FileUtils.REQUEST_CAMERA) {
                fileUtils.onCaptureImageResult(data, uploadedImage);
                uploadedVideo.setVisibility(View.GONE);
                uploadedImage.setVisibility(View.VISIBLE);
            }
            else if (requestCode == FileUtils.VIDEO_CAMERA) {

                fileUtils.onRecordVideoResult(AddEditTaskEntry.this,data, uploadedVideo);
                uploadedImage.setVisibility(View.GONE);
                uploadedVideo.setVisibility(View.VISIBLE);

            }
        }
    }
}
