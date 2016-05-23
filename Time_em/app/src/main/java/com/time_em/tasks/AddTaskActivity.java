package com.time_em.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends Activity implements View.OnClickListener, AsyncResponseTimeEm, AddTaskPresenter.IAddTaskView, SurfaceHolder.Callback {
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @SuppressWarnings("deprecation")
    public String uploadFileToserver() {
        FileBody videoBody, imgBody;

        String serverResponse = "";
        try {

//			long fileLength = file.length();
//			Log.d("", "fileLength " + fileLength);

            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(upLoadServerUri);

            @SuppressWarnings("deprecation")
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);


            File file1 = new File(ImagePathUri);
            imgBody = new FileBody(file1);
            reqEntity.addPart("img", imgBody);

            reqEntity.addPart("ActivityId", new StringBody(patrolID));
            reqEntity.addPart("officer_id", new StringBody(
                    DashboardActivity.officer.getOfficerId()));
            reqEntity.addPart("shift_id", new StringBody(
                    DashboardActivity.officer.getShiftId()));

            reqEntity.addPart("event_name", new StringBody("MME"));
            reqEntity.addPart("latitude",
                    new StringBody(String.valueOf(DashboardActivity.myLat)));
            reqEntity.addPart("longitude",
                    new StringBody(String.valueOf(DashboardActivity.myLon)));
            reqEntity.addPart("checkpoint_id",
                    new StringBody(scannedCheckPoint));
            reqEntity.addPart("text", new StringBody(en_notes_str));
            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                serverResponse = EntityUtils.toString(resEntity);
                Log.i("RESPONSE", serverResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    public class Soap {



        public static String AudioVideoBaseURL = "http://abcd.com";



        public static String getSoapResponseForVideoAudio(String postFixOfUrl,
                                                          List nameValuePairs,
                                                          List filenameValuePairs) {
            String xmlString = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(AudioVideoBaseURL + postFixOfUrl);

            try {
                MultipartEntity entity = new MultipartEntity();

                for (int index = 0; index < filenameValuePairs.size(); index++) {
                    File myFile = new File(filenameValuePairs.get(index).getValue());
                    FileBody fileBody = new FileBody(myFile);
                    entity.addPart(filenameValuePairs.get(index).getName(),
                            fileBody);
                }

                for (int index = 0; index < nameValuePairs.size(); index++) {

                    entity.addPart(nameValuePairs.get(index).getName(),
                            new StringBody(nameValuePairs.get(index).getValue(),
                                    Charset.forName("UTF-8")));

                }

                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost, localContext);
                HttpEntity r_entity = response.getEntity();
                xmlString = EntityUtils.toString(r_entity);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return xmlString.toString();
        }


        public static String apiUploadSong(int userId, int songId,
                                           String songTitle, String songArtist, String isVideo, String fileData)
                throws ClientProtocolException, IOException {

            ArrayList alNameValuePairsFile = new ArrayList();
            NameValuePair nameValuePairsFile = new BasicNameValuePair("fileData",
                    fileData);
            alNameValuePairsFile.add(nameValuePairsFile);

            ArrayList alNameValuePairs = new ArrayList();

            NameValuePair nameValuePairs = new BasicNameValuePair("userId", ""
                    + userId);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songId", ""+songId);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songTitle", songTitle);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songArtist", songArtist);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("isVideo", isVideo);
            alNameValuePairs.add(nameValuePairs);

            String result = Soap.getSoapResponseForVideoAudio(
                    "?action=save_video_audio", alNameValuePairs,
                    alNameValuePairsFile);
            Log.e("SOAP", "save_video_audio : " + result);

            return result;
        }
    }

    public class Soap {
        public String AudioVideoBaseURL = "http://abcd.com";
        public String getSoapResponseForVideoAudio(String postFixOfUrl,
                                                   List nameValuePairs,
                                                   List filenameValuePairs) {
            String xmlString = null;
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(AudioVideoBaseURL + postFixOfUrl);

            try {
                MultipartEntity entity = new MultipartEntity();

                for (int index = 0; index < filenameValuePairs.size(); index++) {
                    File myFile = new File(filenameValuePairs.get(index).getValue());
                    FileBody fileBody = new FileBody(myFile);
                    entity.addPart(filenameValuePairs.get(index).getName(),
                            fileBody);
                }

                for (int index = 0; index < nameValuePairs.size(); index++) {

                    entity.addPart(nameValuePairs.get(index).getName(),
                            new StringBody(nameValuePairs.get(index).getValue(),
                                    Charset.forName("UTF-8")));

                }

                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost, localContext);
                HttpEntity r_entity = response.getEntity();
                xmlString = EntityUtils.toString(r_entity);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return xmlString.toString();
        }


        public  String apiUploadSong(int userId, int songId,
                                           String songTitle, String songArtist, String isVideo, String fileData)
                throws ClientProtocolException, IOException {

            ArrayList alNameValuePairsFile = new ArrayList();
            NameValuePair nameValuePairsFile = new BasicNameValuePair("fileData",
                    fileData);
            alNameValuePairsFile.add(nameValuePairsFile);

            ArrayList alNameValuePairs = new ArrayList();

            NameValuePair nameValuePairs = new BasicNameValuePair("userId", ""
                    + userId);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songId", ""+songId);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songTitle", songTitle);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("songArtist", songArtist);
            alNameValuePairs.add(nameValuePairs);
            nameValuePairs = new BasicNameValuePair("isVideo", isVideo);
            alNameValuePairs.add(nameValuePairs);

            String result = Soap.getSoapResponseForVideoAudio("?action=save_video_audio", alNameValuePairs,alNameValuePairsFile);
            Log.e("SOAP", "save_video_audio : " + result);

            return result;
        }
    }

}
