package com.time_em.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddTaskActivity extends Activity implements View.OnClickListener, AsyncResponseTimeEm, AddTaskPresenter.IAddTaskView, SurfaceHolder.Callback {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private LinearLayout AddTaskBtn;
    private LinearLayout uploadLayout;
    private ImageView UploadImage, back, add;
    private EditText CommentEdit;
    private EditText NumberHoursEdit;
    private TextView headerText;

    private Uri fileUri;
    private byte[] byteArray;

    private int UserId;
    private int ActivityId;
    private int TaskId;
    private String TaskName;
    private String CreatedDate;
    private String AddNewtask;
    private String ImagePathUri, NumberOfHoursStr, CommentStr;
    private String BaseEncodingStr;
    private String selectedDate;

    public CameraHelper cameraHelper;
    private Time_emJsonParser parser;
    private AddTaskPresenter presenter;
    private TaskEntry taskEntry;
    private ProgressDialog pDialog;
    private ArrayAdapter<String> adapterstate;
    Activity act;

    private ArrayList<TaskEntry> taskEntries;
    ArrayList<String> taskEntryStr;
    private Spinner SpnTaskName;

    String url = "http://timeemapi.azurewebsites.net/api/Usertask/AddUpdateUserTaskActivity";
    String id = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        act = this;
        UserId = HomeActivity.user.getId();
        ActivityId = HomeActivity.user.getActivityId();
        selectedDate = getIntent().getStringExtra("selectedDate");
        AddNewtask = getIntent().getStringExtra("AddNewtask");

        taskEntry = (TaskEntry) getIntent().getParcelableExtra("taskEntry");
        if (AddNewtask == null) {
            AddNewtask = String.valueOf(taskEntry.getId());
            TaskId = taskEntry.getTaskId();
        }
        presenter = new AddTaskPresenter(this, act);

        InitView();
        parser = new Time_emJsonParser(AddTaskActivity.this);
    }

    private void InitView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        add = (ImageView)findViewById(R.id.AddButton);
        add.setVisibility(View.GONE);

        NumberHoursEdit = (EditText) findViewById(R.id.NumberHoursEdit);
        CommentEdit = (EditText) findViewById(R.id.CommentEdit);
        AddTaskBtn = (LinearLayout) findViewById(R.id.AddTaskBtn);
        headerText = (TextView)findViewById(R.id.headerText);

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

        if (Utils.isNetworkAvailable(act)) {
            presenter.Init(UserId, taskEntry);
        } else {
            Utils.alertMessage(act, Utils.network_error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadLayout:
                selectImage();
                break;
            case R.id.AddTaskBtn:
                NumberOfHoursStr = NumberHoursEdit.getText().toString();
                CommentStr = CommentEdit.getText().toString();
                if (Utils.isNetworkAvailable(act)) {
                    // new uploadimage().execute();
                    presenter.Init(ActivityId, UserId, NumberOfHoursStr, CommentStr, TaskId, TaskName, selectedDate, AddNewtask);
                } else {
                    Utils.alertMessage(act, Utils.network_error);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

//    /**
//     * Receiving activity result method will be called after closing the camera
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // if the result is capturing Image
//        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // bimatp factory
//                BitmapFactory.Options options = new BitmapFactory.Options();
//
//                // downsizing image as it throws OutOfMemory Exception for
//                options.inSampleSize = 8;
//                ImagePathUri = fileUri.getPath();
//
//                final Bitmap bitmap = BitmapFactory.decodeFile(ImagePathUri, options);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byteArray = stream.toByteArray();
//                // Encode Image to String
//                BaseEncodingStr = Base64.encodeToString(byteArray, 0);
//                Log.d("", "BaseEncodingStr" + BaseEncodingStr);
//
//                UploadImage.setVisibility(View.VISIBLE);
//                UploadImage.setImageBitmap(ExifUtils.rotateBitmap(
//                        fileUri.getPath(), bitmap));
//
//            }
//
//        } else if (resultCode == RESULT_CANCELED) {
//
//            // user cancelled recording
//            Toast.makeText(getApplicationContext(),
//                    "User cancelled video recording", Toast.LENGTH_SHORT)
//                    .show();
//
//        } else {
//            // failed to record video
//            Toast.makeText(getApplicationContext(),
//                    "Sorry! Failed to record video", Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }

    protected void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                AddTaskActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the
                    // intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            // mCurrentPhotoPath = Util.createImageFile();

                            // photoFile = new File(mCurrentPhotoPath);
                        } catch (Exception ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    // check=1;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"), 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String imagePath;
            if (requestCode == 1) {

                Bitmap bitmap = BitmapFactory.decodeFile("");
                Bitmap bm2 = ExifUtils.rotateBitmap("", bitmap);

                UploadImage.setImageBitmap(bm2);
                imagePath = SaveImage(bm2);

                //new uploadimage().execute();

            } else if (requestCode == 2) {


                if (Build.VERSION.SDK_INT < 19) {

                    Uri selectedImageUri = data.getData();

                    Cursor cursor = getContentResolver()
                            .query(selectedImageUri,
                                    new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
                                    null, null, null);
                    cursor.moveToFirst();

                    // Link to the image
                    final String imageFilePath = cursor.getString(0);
                    cursor.close();
                    imagePath = imageFilePath;

                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    Bitmap bm2 = ExifUtils.rotateBitmap(imagePath, bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    // Encode Image to String
                    //BaseEncodingStr = Base64.encodeToString(byteArray, 0);
                    UploadImage.setImageBitmap(bm2);
                    BaseEncodingStr = SaveImage(bm2);
                } else {
                    try {
                        InputStream imInputStream = getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                        // Encode Image to String
                        // BaseEncodingStr = Base64.encodeToString(byteArray, 0);

                        BaseEncodingStr = saveGalaryImageOnLitkat(bitmap);
                        UploadImage.setImageBitmap(BitmapFactory.decodeFile(BaseEncodingStr));
                        //encodeImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
                // new uploadimage().execute();

            }
            // Log.d("encodedImage=",encodedImage);
        }
    }

    private File temp_path;
    private final int COMPRESS = 100;

    private String saveGalaryImageOnLitkat(Bitmap bitmap) {
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
            else
                cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            //  file.createNewFile();
            FileOutputStream out = new FileOutputStream(temp_path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
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

    public void LoadSpinnerData(ArrayList<String> taskEntryStr, ArrayList<TaskEntry> taskEntries) {
        this.taskEntries = taskEntries;
        this.taskEntryStr = taskEntryStr;

        adapterstate = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, taskEntryStr);
        adapterstate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnTaskName.setAdapter(adapterstate);

        if (!TaskName.equals(null)) {
            int spinnerPosition = adapterstate.getPosition(TaskName);
            SpnTaskName.setSelection(spinnerPosition);
        }
    }

    @Override
    public void LoadCreateDate(String CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    @Override
    public void LoadComment(String commentStr) {
        CommentEdit.setText(commentStr);
    }

    @Override
    public void LoadTimeSpent(Double TimeSpent) {
        NumberHoursEdit.setText(String.valueOf(TimeSpent));
    }

    @Override
    public void LoadTaskName(String TaskName) {
        this.TaskName = TaskName;
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

    private class uploadimage extends AsyncTask<Void, Void, Void> { // Async_task
        // class
        String res;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AddTaskActivity.this);

            // pDialog.setTitle("Loading");
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {//because i need growth

                String upLoadServerUri = url + "ActivityId=" + ActivityId + "&TaskId=" + TaskId + "&UserId=" + UserId + "&TimeSpent=" + NumberOfHoursStr + "&CreatedDate=05-20-2016&Comments=" + CommentStr + "&TaskName=" + TaskName + "&ID =" + id;

                Log.d("", "upLoadServerUri" + upLoadServerUri);
                Log.d("", "upLoadServerUri" + upLoadServerUri);
                Log.d("", "BaseEncodingStr" + BaseEncodingStr);
                res = multipartRequest(upLoadServerUri, BaseEncodingStr);
                // res = uploadFileToserver();
                Log.d("", "resres" + res);
                // uploadFile1(imagePath);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }

        // image upload in multi part
        public String multipartRequest(String urlTo, String filepath)
                throws ParseException, IOException {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            InputStream inputStream = null;

            String twoHyphens = "--";
            String boundary = "*****" + Long.toString(System.currentTimeMillis())
                    + "*****";
            String lineEnd = "\r\n";

            String result = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            String[] q = filepath.split("/");
            int idx = q.length - 1;

            try {
                File file = new File(filepath);
                FileInputStream fileInputStream = new FileInputStream(file);

                URL url = new URL(urlTo);
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent",
                        "Android Multipart HTTP Client 1.0");
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream
                        .writeBytes("Content-Disposition: form-data;filename=\""
                                + q[idx] + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary"
                        + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);

                inputStream = connection.getInputStream();
                result = this.convertStreamToString(inputStream);
                fileInputStream.close();
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                Log.e("Multipart result", result);
                return result;
            } catch (Exception e) {
                Log.e("MultipartRequest", "Multipart Form Upload Error");
                e.printStackTrace();
                return "error";
            }
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }


    public String uploadFileToserver() {
        FileBody videoBody, imgBody;

        String serverResponse = "";
        try {

            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(url);


            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

//            if(!filepath.equals("")){
//
//                File file = new File(filepath);
//                videoBody = new FileBody(file);
//                reqEntity.addPart("video", videoBody);
//            }

            // "&timespant=" + NumberOfHoursStr + "&createddate=05-20-2016&comments=" + CommentStr;
            File file1 = new File(BaseEncodingStr);
            imgBody = new FileBody(file1);
            reqEntity.addPart("ActiIvityId", imgBody);


            reqEntity.addPart("ActiIvityId", new StringBody(String.valueOf(ActivityId)));
            reqEntity.addPart("Taskd", new StringBody(String.valueOf(TaskId)));
            reqEntity.addPart("UserId", new StringBody(String.valueOf(UserId)));
            reqEntity.addPart("TimeSpent", new StringBody(NumberOfHoursStr));
            reqEntity.addPart("CreatedDate", new StringBody("05-20-2016"));
            reqEntity.addPart("Comments", new StringBody(String.valueOf(CommentStr)));
            reqEntity.addPart("ID", new StringBody(String.valueOf(CommentStr)));

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

    public static String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));
        String body = "";
        String content = "";

        while ((body = rd.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }

}
