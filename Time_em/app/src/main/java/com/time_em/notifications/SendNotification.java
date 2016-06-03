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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
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
    private TextView recipients, upload;
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
        recipients = (TextView) findViewById(R.id.spnUsers);
        upload = (TextView) findViewById(R.id.upload);
        uploadedImage = (ImageView) findViewById(R.id.uploadedImage);
        sendNotification = (Button) findViewById(R.id.send);

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
                sendNotification();
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
                    android.R.layout.simple_spinner_item);
            spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        }
    }

    private void showUserSelectionDropdown(){
        Intent intent = new Intent(SendNotification.this, UserSelectionActivity.class);

        intent.putExtra("activeUsers", userList);
        intent.putExtra("selectedIds", selectedIds);

        startActivity(intent);
    }
    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    private byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void sendNotification(){
        String url = "http://timeemapi.azurewebsites.net/api/Notification/AddNotification";
       File file = new File(attachmentPath);
        int size = (int) file.length();

        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//
        byte[] fileData1 = getFileDataFromDrawable(SendNotification.this, R.drawable.ic_launcher);
//        byte[] fileData2 = getFileDataFromDrawable(context, R.drawable.ic_action_book);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // the first file
            buildTextPart(dos, "UserId", String.valueOf(HomeActivity.user.getId()));
            buildTextPart(dos, "Subject", subject.getText().toString());
            buildTextPart(dos, "Message", message.getText().toString());
            buildTextPart(dos, "NotificationTypeId", selectedNotificationTypeId);
            buildTextPart(dos, "notifyto", selectedIds);
            buildPart(dos, bytes, "temp.png");

            // send multipart form data necesssary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // pass to multipart body
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        MultipartRequest mCustomRequest = new MultipartRequest(url, null, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast.makeText(SendNotification.this, "Upload successfully!", Toast.LENGTH_SHORT).show();
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.e("volley response", ":::"+jsonString);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("volley response", ":::"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SendNotification.this, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

       /* //Auth header
        Map<String, String> mHeaderPart= new HashMap<>();
        mHeaderPart.put("Content-type", "multipart/form-data;");

////File part
        Map<String, File> mFilePartData= new HashMap<>();
//        mFilePartData.put("file", new File(attachmentPath));

//String part
        Map<String, String> mStringPart= new HashMap<>();
        mStringPart.put("UserId", String.valueOf(HomeActivity.user.getId()));
        mStringPart.put("Subject", subject.getText().toString());
        mStringPart.put("Message", message.getText().toString());
        mStringPart.put("NotificationTypeId", selectedNotificationTypeId);
        mStringPart.put("notifyto", selectedIds);

        CustomMultipartRequest mCustomRequest = new CustomMultipartRequest(Request.Method.POST, SendNotification.this, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                listener.onResponse(jsonObject);
                Toast.makeText(SendNotification.this, "Upload successfully! ", Toast.LENGTH_SHORT).show();
                Log.e("volley json response","::: "+jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SendNotification.this, "Upload failed!\r\n" + volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        }, mFilePartData, mStringPart, mHeaderPart);
*/
        mCustomRequest.setShouldCache(false);
        VolleySingleton.getInstance(SendNotification.this).addToRequestQueue(mCustomRequest);

       /* String res="";

        String charset = "UTF-8";
        String requestURL = "http://timeemapi.azurewebsites.net/api/Notification/AddNotification";


        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

//            multipart.addHeaderField("User-Agent", "CodeJava");
//            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("LoginId", String.valueOf(HomeActivity.user.getId()));
            multipart.addFormField("Subject", subject.getText().toString());
            multipart.addFormField("Message", message.getText().toString());
            multipart.addFormField("NotificationTypeId", selectedNotificationTypeId);
            multipart.addFormField("notifyto", selectedIds);
            multipart.addFilePart("fileUpload", new File(attachmentPath));

            List<String> response = multipart.finish();

            for (String line : response) {
               res = res + "," +line;
            }

        } catch (IOException ex) {
            res = ex.getMessage();
            ex.printStackTrace();
        }

        return res;*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedIds = Utils.getSharedPrefs(SendNotification.this, "SelectedIds");
        selectedUsers = Utils.getSharedPrefs(SendNotification.this, "SelectedUsers");
        recipients.setText(selectedUsers);
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
        /*Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
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
//            Bitmap mImageBitmap = MediaStore.Images.Media.getBitmap(SendNotification.this.getContentResolver(), Uri.parse(attachmentPath));
            uploadedImage.setImageBitmap(getScaledBitmap(attachmentPath, 800, 800));
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");
        attachmentPath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadedImage.setImageBitmap(thumbnail);*/
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

   /* public class AsyncTaskNotification extends AsyncTask<String, Void, String> {


        private ProgressDialog pDialog;
        private String response;

        public AsyncTaskNotification(){

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

                pDialog = new ProgressDialog(SendNotification.this);
                pDialog.setTitle("Time'em");
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            ContentBody contentPart = new ByteArrayBody(bos.toByteArray(), filename);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("picture", contentPart);
            String response = multipost("http://server.com", reqEntity);
            return "success";
        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            int resultcode=0;
            try{
                pDialog.dismiss();

                Utils.alertMessage(SendNotification.this, response);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/
    private static String multipost(String urlString, MultipartEntity reqEntity) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.addRequestProperty("Content-length", reqEntity.getContentLength()+"");
            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());

            OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            os.close();
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readStream(conn.getInputStream());
            }

        } catch (Exception e) {
            Log.e("Time'em", "multipart post error " + e + "(" + urlString + ")");
        }
        return null;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}