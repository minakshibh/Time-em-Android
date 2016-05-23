package com.time_em.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.TaskEntry;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.CameraHelper;
import com.time_em.utils.ExifUtils;
import com.time_em.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AddTaskActivity extends Activity implements View.OnClickListener, AsyncResponseTimeEm, AddTaskPresenter.IAddTaskView {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private LinearLayout AddTaskBtn;
    private LinearLayout uploadLayout;
    private ImageView UploadImage;
    private EditText CommentEdit;
    private EditText NumberHoursEdit;

    private Uri fileUri;
    private byte[] byteArray;

    private int UserId;
    private int ActivityId;
    private int TaskId;
    private String TaskName;
    private String ImagePathUri;
    private String BaseEncodingStr;

    public CameraHelper cameraHelper;
    private Time_emJsonParser parser;
    private AddTaskPresenter presenter;

    private ProgressDialog pDialog;

    Activity act;

    private ArrayList<TaskEntry> taskEntries;
    private Spinner SpnTaskName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        act = this;
        UserId = HomeActivity.user.getId();
        ActivityId = HomeActivity.user.getActivityId();

        presenter = new AddTaskPresenter(this, act);

        if (Utils.isNetworkAvailable(act)) {
            presenter.Init(UserId);
        } else {
            Utils.alertMessage(act, Utils.network_error);
        }

        InitView();
        parser = new Time_emJsonParser(AddTaskActivity.this);
    }

    private void InitView() {
        NumberHoursEdit = (EditText) findViewById(R.id.NumberHoursEdit);
        CommentEdit = (EditText) findViewById(R.id.CommentEdit);
        AddTaskBtn = (LinearLayout) findViewById(R.id.AddTaskBtn);
        AddTaskBtn.setOnClickListener(this);

        UploadImage = (ImageView) findViewById(R.id.UploadImage);
        uploadLayout = (LinearLayout) findViewById(R.id.uploadLayout);
        uploadLayout.setOnClickListener(this);

        SpnTaskName = (Spinner) findViewById(R.id.SpnTaskName);
        SpnTaskName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                TaskId = taskEntries.get(position).getTaskId();
                TaskName = taskEntries.get(position).getTaskName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadLayout:
                cameraHelper = new CameraHelper();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = cameraHelper.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                // start the image capture Intent
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                break;
            case R.id.AddTaskBtn:
                String NumberOfHoursStr = NumberHoursEdit.getText().toString();
                String CommentStr = CommentEdit.getText().toString();
                if (Utils.isNetworkAvailable(act)) {
                    presenter.Init(ActivityId, UserId, NumberOfHoursStr, CommentStr, TaskId, TaskName, "555", "1");
                } else {
                    Utils.alertMessage(act, Utils.network_error);
                }

        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for
                options.inSampleSize = 8;
                ImagePathUri = fileUri.getPath();

                final Bitmap bitmap = BitmapFactory.decodeFile(ImagePathUri, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                // Encode Image to String
                BaseEncodingStr = Base64.encodeToString(byteArray, 0);
                Log.d("", "BaseEncodingStr" + BaseEncodingStr);

                UploadImage.setVisibility(View.VISIBLE);
                UploadImage.setImageBitmap(ExifUtils.rotateBitmap(
                        fileUri.getPath(), bitmap));

            }

        } else if (resultCode == RESULT_CANCELED) {

            // user cancelled recording
            Toast.makeText(getApplicationContext(),
                    "User cancelled video recording", Toast.LENGTH_SHORT)
                    .show();

        } else {
            // failed to record video
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        if (methodName.equalsIgnoreCase(Utils.GetAddUpdateUserTaskAPI)) {
            Utils.alertMessage(AddTaskActivity.this, output);
        } else {
            taskEntries = parser.parseSpinnneData(output, UserId);
            presenter.SpinnerApiData(taskEntries);
        }
    }

    public void LoadSpinnerData(ArrayList<String> taskEntry, ArrayList<TaskEntry> taskEntries) {
        this.taskEntries = taskEntries;

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, taskEntry);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnTaskName.setAdapter(adapter_state);
    }
}
