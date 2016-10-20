package com.time_em.tasks;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.time_em.utils.PrefUtils;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class AddEditTaskEntry extends Activity implements AsyncResponseTimeEm {

    //todo widgets
    private TextView txtProjectSelection, txtCommentsHeader, txtHoursHeader, headerInfo, dateHeader,first_separator;
    private MySpinner spnProject;
    private ImageView hoursIcon, uploadedImage, back, rightNavigation,imgdelete, videodelete;
    private LinearLayout recipientSection, uploadAttachment;
    private RelativeLayout notTypeLayout;
    private Button addUpdateTask;
    private EditText hours, comments;
   //todo classes
    private Time_emJsonParser parser;
    private SpinnerTypeAdapter adapter;
    private SpinnerData selectedSpinnerData;
    private FileUtils fileUtils;
    private TaskEntry taskEntry=null;
    private JCVideoPlayerStandard uploadedVideo;
    private TimeEmDbHandler dbHandler;
    //todo variables
    public static String UniqueNumber="";
    private String newTaskName="",UserId="";
    private String addUpdateTaskAPI = Utils.AddUpdateUserTaskAPI, selectedDate, taskEntryId = "0",taskId="0";
    //todo array list
    private ArrayList<SpinnerData> assignedTasks=new ArrayList<>();
    ArrayList<TaskEntry> taskEntries = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_send_notification);

        initScreen();
        loadProjects();
        setListeners();
        UniqueNumber= FileUtils.getUniqueNumber(AddEditTaskEntry.this);
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
        hoursIcon = (ImageView) findViewById(R.id.messageIcon);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        uploadedVideo=(JCVideoPlayerStandard)findViewById(R.id.uploadedVideo);
        recipientSection =(LinearLayout)findViewById(R.id.recipientSection);
        uploadAttachment = (LinearLayout)findViewById(R.id.upload);
        addUpdateTask = (Button)findViewById(R.id.send);
        headerInfo = (TextView)findViewById(R.id.headerText);
        back =(ImageView)findViewById(R.id.back);
        rightNavigation = (ImageView)findViewById(R.id.AddButton);
        rightNavigation.setVisibility(View.GONE);
        dateHeader = (TextView)findViewById(R.id.dateHeader);

        imgdelete=(ImageView) findViewById(R.id.imgdelete);
        videodelete=(ImageView) findViewById(R.id.videodelete);

        selectedDate = getIntent().getStringExtra("selectDate");
        taskEntry = getIntent().getParcelableExtra("taskEntry");
        UserId = getIntent().getStringExtra("UserId");

        imgdelete.setVisibility(View.GONE);
        videodelete.setVisibility(View.GONE);

        dateHeader.setVisibility(View.VISIBLE);
        dateHeader.setText(selectedDate);


        //todo for task view chnages in xml
        first_separator=(TextView)findViewById(R.id.first_separator);
        first_separator.setVisibility(View.VISIBLE);
        notTypeLayout=(RelativeLayout)findViewById(R.id.notTypeLayout);
        notTypeLayout.setVisibility(View.VISIBLE);
        spnProject = (MySpinner) findViewById(R.id.spnNotificationType);
        spnProject.setVisibility(View.VISIBLE);

        txtProjectSelection.setVisibility(View.VISIBLE);
        txtProjectSelection.setText("Select Project or Task:");
        txtCommentsHeader.setText("Enter your Comments:");
        comments.setHint("Your comments goes here");
        txtHoursHeader.setText("Specify number of hours:");
        hours.setHint("No. of hours.(max 12hrs)");
        hours.setInputType(InputType.TYPE_CLASS_NUMBER);
        int maxLength = 2;
        hours.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        hoursIcon.setImageResource(R.drawable.user_icon);
        recipientSection.setVisibility(View.GONE);
        addUpdateTask.setText("ADD");
        addUpdateTask.setTextSize(20);
        assignedTasks = new ArrayList<>();

        if(taskEntry == null) {
            headerInfo.setText("Add Task");
        }else{
            headerInfo.setText("Edit Task");
            taskEntryId = String.valueOf(taskEntry.getId());
            taskId=String.valueOf(taskEntry.getTaskId());
            comments.setText(taskEntry.getComments());

            hours.setText(String.valueOf(taskEntry.getTimeSpent()));
            String image_url = taskEntry.getAttachmentImageFile();
            Log.e("image_url",""+image_url);
            int loader = R.drawable.loader;
            // ImageLoader class instance
            if (image_url != null && !image_url.equalsIgnoreCase("null")) {
                if(image_url.contains("http")) {
                    if(image_url.contains(".jpg") | image_url.contains(".png")) {
                        uploadedImage.setVisibility(View.VISIBLE);
                        uploadedVideo.setVisibility(View.GONE);
                        imgdelete.setVisibility(View.VISIBLE);
                        videodelete.setVisibility(View.GONE);
                        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                        imgLoader.DisplayImage(image_url, loader, uploadedImage);
                    }else{
                        uploadedImage.setVisibility(View.GONE);
                        uploadedVideo.setVisibility(View.VISIBLE);
                        videodelete.setVisibility(View.VISIBLE);
                        imgdelete.setVisibility(View.GONE);
                        uploadedVideo.setUp(image_url , "video");
                      /*uploadedVideo.setVideoPath(image_url);
                        uploadedVideo.setMediaController(new MediaController(AddEditTaskEntry.this));
                        uploadedVideo.requestFocus();
                        uploadedVideo.seekTo(5);*/
                    }
                }else{

                    if(image_url.contains(".jpg") | image_url.contains(".png")) {
                        uploadedImage.setVisibility(View.VISIBLE);
                        uploadedVideo.setVisibility(View.GONE);
                        imgdelete.setVisibility(View.VISIBLE);
                        videodelete.setVisibility(View.GONE);
                        FileUtils fileUtils = new FileUtils(AddEditTaskEntry.this);
                        uploadedImage.setImageBitmap(fileUtils.getScaledBitmap(image_url, 300, 250));
                    }
                    else{
                        uploadedImage.setVisibility(View.GONE);
                        uploadedVideo.setVisibility(View.VISIBLE);
                        videodelete.setVisibility(View.VISIBLE);
                        imgdelete.setVisibility(View.GONE);
                        try {
                            uploadedVideo.setUp(image_url, "video");
                        }catch (Exception e)
                        {e.printStackTrace();
                        }
                      /*uploadedVideo.setVideoPath(image_url);
                        uploadedVideo.setMediaController(new MediaController(AddEditTaskEntry.this));
                        uploadedVideo.requestFocus();
                        uploadedVideo.seekTo(5);*/
                    }
                }
            }
        }
    }

    private void selectedSpinnerValue(Spinner sp) {

        Log.e("taskId", "" + taskId);
        if(assignedTasks!=null && !assignedTasks.equals("null")){
            for(int i=0;i<assignedTasks.size();i++)
            {
               String value=""+assignedTasks.get(i).getId();
                    if(taskId.equalsIgnoreCase(value)) {
                        if(assignedTasks.get(i).getId() != 0)
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
        imgdelete.setOnClickListener(listener);
        videodelete.setOnClickListener(listener);
        // You can create an anonymous listener to handle the event when is selected an spinner item

        spnProject.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                selectedSpinnerData = adapter.getItem(position);
                // Here you can do the action you want to...
                // selectedProjectId = String.valueOf(project.getId());
                //for add new task
                if (selectedSpinnerData.getId() == 0) {
                    showAddNewTask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int getSPrefsId=0;
            Double intHours=0.0;
            try {
                getSPrefsId = Integer.parseInt(Utils.getSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER_ID));
                intHours=Double.parseDouble(hours.getText().toString().trim());
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            if(v == back){
                finish();
            }else if(v == uploadAttachment){
                fileUtils.showChooserDialog(true);
            }else if(v == imgdelete || v == videodelete){
                uploadedImage.setVisibility(View.GONE);
                uploadedVideo.setVisibility(View.GONE);
                imgdelete.setVisibility(View.GONE);
                videodelete.setVisibility(View.GONE);
                fileUtils.setAttachmentPath(null);
            }else if(v == addUpdateTask){

                if(comments.getText().toString().trim().equals("") || hours.getText().toString().trim().equals("") || selectedSpinnerData == null){
                    Utils.showToast(AddEditTaskEntry.this, "Please specify required information");
                }
                else if(selectedSpinnerData.getId()==-1){
                    Utils.showToast(AddEditTaskEntry.this, "Please select specify project/task");
                }
                else if(intHours>12){
                    Utils.showToast(AddEditTaskEntry.this, "Please enter hours values less than 12 hrs.");
                }
                else if(!PrefUtils.getBooleanPreference(getApplicationContext(),PrefUtils.KEY_IS_SIGNED_IN,false)){
                   Utils.alertMessage(AddEditTaskEntry.this, "You are currently signed out. To continue Please sign in.");
                }
              else {


                      if (Utils.isNetworkAvailable(AddEditTaskEntry.this)) {
                            HashMap<String, String> postDataParameters = new HashMap<String, String>();
                            postDataParameters.put("UserId", String.valueOf(UserId));
                            postDataParameters.put("ActivityId",
                                     String.valueOf(PrefUtils.getIntegerPreference(getApplicationContext(),PrefUtils.KEY_ACTIVITY_ID,0)));
                            postDataParameters.put("TimeSpent", hours.getText().toString());
                            postDataParameters.put("Comments", comments.getText().toString());
                            postDataParameters.put("CompanyId",
                                   PrefUtils.getStringPreference(AddEditTaskEntry.this,PrefUtils.KEY_COMPANY));
                            if (selectedSpinnerData.getId() == 0) {
                                postDataParameters.put("TaskId", String.valueOf(0));
                                if(newTaskName != null)
                                postDataParameters.put("TaskName", newTaskName);
                                else
                                Utils.showToast(AddEditTaskEntry.this, "Please select specify project/task");
                            }else{
                                postDataParameters.put("TaskId", String.valueOf(selectedSpinnerData.getId()));
                                postDataParameters.put("TaskName", String.valueOf(selectedSpinnerData.getName()));
                            }
                            postDataParameters.put("CreatedDate", selectedDate);
                            postDataParameters.put("ID", taskEntryId);

                            Log.e(""+Utils.AddUpdateUserTaskActivityNew,""+postDataParameters.toString());
                            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                                    AddEditTaskEntry.this, "post", Utils.AddUpdateUserTaskActivityNew,
                                    postDataParameters, true, "Please wait...");
                            mWebPageTask.delegate = (AsyncResponseTimeEm) AddEditTaskEntry.this;
                            mWebPageTask.execute();
                              //  fileUtils.sendMultipartRequest(addUpdateTaskAPI, dataModels);
                            } else {

                          /*  dataModels.add(new MultipartDataModel("UserId", ""+getSPrefsId, MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("ActivityId", String.valueOf(HomeActivity.user.getActivityId()), MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("TimeSpent", hours.getText().toString(), MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("Comments", comments.getText().toString(), MultipartDataModel.STRING_TYPE));
                            if (selectedSpinnerData.getId() == 0) {
                                dataModels.add(new MultipartDataModel("TaskId", String.valueOf(0), MultipartDataModel.STRING_TYPE));
                                dataModels.add(new MultipartDataModel("TaskName", newTaskName, MultipartDataModel.STRING_TYPE));
                            } else {
                                dataModels.add(new MultipartDataModel("TaskId", String.valueOf(selectedSpinnerData.getId()), MultipartDataModel.STRING_TYPE));
                                dataModels.add(new MultipartDataModel("TaskName", String.valueOf(selectedSpinnerData.getName()), MultipartDataModel.STRING_TYPE));
                            }
                            dataModels.add(new MultipartDataModel("CreatedDate", selectedDate, MultipartDataModel.STRING_TYPE));
                            dataModels.add(new MultipartDataModel("ID", taskEntryId, MultipartDataModel.STRING_TYPE));*/


                                TaskEntry task = new TaskEntry();
                                if (fileUtils.getAttachmentPath() != null)
                                    task.setAttachmentImageFile(fileUtils.getAttachmentPath());
                                task.setId(Integer.parseInt(taskEntryId));
                               // task.setActivityId(HomeActivity.user.getActivityId());
                                task.setActivityId(PrefUtils.getIntegerPreference(getApplicationContext(),PrefUtils.KEY_ACTIVITY_ID,0));
                                if (selectedSpinnerData.getId() == 0) {
                                    task.setTaskId(0);
                                    task.setTaskName(newTaskName);
                                } else {
                                    task.setTaskId(selectedSpinnerData.getId());
                                    task.setTaskName(selectedSpinnerData.getName());
                                }

                                task.setUserId(getSPrefsId);
                                task.setTimeSpent(Double.parseDouble(hours.getText().toString()));
                                task.setComments(comments.getText().toString());
                                task.setSignedInHours(0.0);
                                task.setSelectedDate(selectedDate);
                                task.setIsActive(true);
                                task.setAttachmentImageFile(fileUtils.getAttachmentPath());
                                task.setCompanyId(PrefUtils.getStringPreference(AddEditTaskEntry.this,PrefUtils.KEY_COMPANY));
                                task.setIsoffline("true");
                                String taskDate=selectedDate;
                                  try {
                                      taskDate = Utils.formatDateChange(selectedDate,"MM-dd-yyyy", "dd/MM/yyyy hh:mm:ss");
                                  }catch (Exception e)
                                  {e.printStackTrace();}
                                task.setCreatedDate(taskDate);
                                task.setToken("00");
                                // task.setEndTime(taskObject.getString("EndTime"));
                                // task.setStartTime(taskObject.getString("StartTime"));

                                if (taskEntry == null) {   //for add new offline

                                    // task.setStartTime(taskObject.getString("StartTime"));

                                    Log.e("UniqueNumber", "" + UniqueNumber);

                                    task.setToken("00");
                                    task.setUniqueNumber(UniqueNumber);
                                    taskEntries.add(task);
                                    dbHandler.updateTask(taskEntries, selectedDate, true);
                                    Utils.alertMessage(AddEditTaskEntry.this, "Task Add Successfully.!");
                                }
                                //for edit delete old offline
                                else {

                                    Log.e("UniqueNumber", "" + UniqueNumber);
                                    // Log.e("milliSecond +id", "" + HomeActivity.user.getId() + "" + timeStamp);

                                    if (taskEntry.getId() == 0) {
                                        task.setUniqueNumber(taskEntry.getUniqueNumber());
                                        taskEntries.add(task);
                                        dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                                        Utils.alertMessage(AddEditTaskEntry.this, "Task Updated Successfully.!");
                                    } else {

                                        task.setUniqueNumber(UniqueNumber);
                                        taskEntries.add(task);
                                        dbHandler.updateTask(taskEntries, selectedDate, false);
                                        Utils.alertMessage(AddEditTaskEntry.this, "Task Updated Successfully.!");
                                    }
                                }

                            }

                        }

            }
        }
    };

    private void loadProjects() {

            int getSPrefsId = Integer.parseInt(Utils.getSharedPrefs(getApplicationContext(),PrefUtils.KEY_USER_ID));
            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("userId", String.valueOf(getSPrefsId));
            postDataParameters.put("CompanyId",
                PrefUtils.getStringPreference(AddEditTaskEntry.this,PrefUtils.KEY_COMPANY));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    AddEditTaskEntry.this, "get", Utils.GetAssignedTaskIList,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) AddEditTaskEntry.this;
            mWebPageTask.execute();


    }

    @Override
    public void processFinish(String output, String methodName) {
        Log.e("output",""+output);
        if(methodName.equals(Utils.GetAssignedTaskIList)){
            String companyId=PrefUtils.getStringPreference(AddEditTaskEntry.this,PrefUtils.KEY_COMPANY);
            assignedTasks = parser.parseAssignedProjects(output,Integer.parseInt(companyId));

            dbHandler.updateProjectTasks(assignedTasks);// // todo update data for notification type

            assignedTasks=dbHandler.getProjectTasksData(companyId);
            adapter = new SpinnerTypeAdapter(AddEditTaskEntry.this, R.layout.spinner_row_layout, assignedTasks);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spnProject.setAdapter(adapter);
            if(taskEntry!=null)
            {
                selectedSpinnerValue(spnProject);
                }
        }
        else if(methodName.equals(Utils.AddUpdateUserTaskActivityNew)){
            String Id = parser.getTaskId(output);
            if(Id.equalsIgnoreCase("0")){
                Utils.showToast(AddEditTaskEntry.this,Utils.Api_error);
            }
            else {
                finish();
                if(fileUtils.getAttachmentPath() !=null) {
                    syncUploadFile(Id);
                }
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
                uploadedVideo.setVisibility(View.GONE);
                videodelete.setVisibility(View.GONE);
                uploadedImage.setVisibility(View.VISIBLE);
                imgdelete.setVisibility(View.VISIBLE);
                fileUtils.onSelectFromGalleryResult(data, uploadedImage);
            }
            else if (requestCode == FileUtils.REQUEST_CAMERA) {
                uploadedVideo.setVisibility(View.GONE);
                videodelete.setVisibility(View.GONE);
                uploadedImage.setVisibility(View.VISIBLE);
                imgdelete.setVisibility(View.VISIBLE);
                fileUtils.onCaptureImageResult(data, uploadedImage);
            }
            else if (requestCode == FileUtils.VIDEO_CAMERA) {
                uploadedImage.setVisibility(View.GONE);
                imgdelete.setVisibility(View.GONE);
                uploadedVideo.setVisibility(View.VISIBLE);
                videodelete.setVisibility(View.VISIBLE);
                fileUtils.onRecordVideoResult(AddEditTaskEntry.this, data, uploadedVideo);
            }
        }
    }

    private  void showAddNewTask()
    {
        //todo  get prompts.xml view
        LayoutInflater li = LayoutInflater.from(AddEditTaskEntry.this);
        View promptsView = li.inflate(R.layout.layout_addnewtask, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddEditTaskEntry.this);

        alertDialogBuilder.setTitle("Add new task");
        // alertDialogBuilder.setMessage("Enter Password");
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editText);

        // set dialog message
        alertDialogBuilder.setCancelable(false) .setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newTaskName = userInput.getText().toString();
                        if (newTaskName.equals("")) {
                            Utils.alertMessage(AddEditTaskEntry.this, "Please enter task name");
                        } else {
                            Utils.hideKeyboard(AddEditTaskEntry.this);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            newTaskName = taskEntry.getTaskName();
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void syncUploadFile(String Id) {
            String ImagePath = fileUtils.getAttachmentPath();
            ArrayList<MultipartDataModel> dataModels = new ArrayList<>();
            dataModels.add(new MultipartDataModel("Id",Id, MultipartDataModel.STRING_TYPE));
            dataModels.add(new MultipartDataModel("FileUploadFor", "usertaskactivity", MultipartDataModel.STRING_TYPE));
             if (ImagePath != null)
                dataModels.add(new MultipartDataModel("profile_picture", ImagePath, MultipartDataModel.FILE_TYPE));
                Log.e("send task", "send task" + ImagePath);
            fileUtils.sendMultipartRequest("other",Utils.SyncFileUpload, dataModels);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // uploadedVideo.sta
    }

    @Override
    protected void onPause() {
        super.onPause();
       // uploadedVideo.setTop(0);
    }


}
