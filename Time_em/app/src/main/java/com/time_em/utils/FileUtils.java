package com.time_em.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.time_em.android.R;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.MultipartDataModel;
import com.time_em.tasks.AddEditTaskEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.protocol.HTTP;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by minakshi on 08/06/16.
 */

public class FileUtils {
    private String userChoosenTask;
    public  String attachmentPath;
    private Context context;
    CharSequence[] items=null;
    public static int SELECT_FILE = 1, REQUEST_CAMERA = 2, VIDEO_CAMERA = 3;

    public FileUtils(Context context){
        this.context = context;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }
    public void setAttachmentPath(String path) {
        this.attachmentPath = path;
    }

    public void showChooserDialog(boolean video) {

        if(video){
            items= new CharSequence[]{"Take Photo", "Choose from Library","Record Video", "Cancel"};
        }else {
            items = new CharSequence[]{"Take Photo", "Choose from Library", "Cancel"};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add File !");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utils.checkPermission(context);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                 }
                else if (items[item].equals("Record Video")) {
                    userChoosenTask="Record Video";
                    if(result)
                    videoIntent();
                }else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    public void cameraIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(false);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                ((Activity)context).startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    public void videoIntent()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createImageFile(true);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                ((Activity)context).startActivityForResult(takeVideoIntent, VIDEO_CAMERA);
            }
        }
    }

    private File createImageFile(boolean video) throws IOException {
        // Create an image file name
       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      //  long timeStamp=System.currentTimeMillis();
       // Log.e("timeStamp",""+timeStamp);
        String imageFileName = "IMG_" + ""+ AddEditTaskEntry.UniqueNumber;// + "_";
        String videoFileName = "VID_" + ""+ AddEditTaskEntry.UniqueNumber;//+ "_";
        Log.e("imageFileName",""+imageFileName);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file;
        if(video)
        {
          //  file = File.createTempFile(
               //     videoFileName,  /* prefix */
              //      ".3gp",         /* suffix */
             //       storageDir      /* directory */
          //  );
            file= new File(storageDir
                    + File.separator + videoFileName+".mp4");
        }
        else {
         //   file = File.createTempFile(
         //           imageFileName,  /*//* prefix */
         //           ".png",         //*//* suffix *
          //          storageDir      /* directory */
            // );

            file= new File(storageDir
                    + File.separator + imageFileName+".png");



            Log.e("file",""+file);
        }

        // Save a file: path for use with ACTION_VIEW intents
      //  attachmentPath = "file:" + file.getAbsolutePath();
        attachmentPath = "" + file.getAbsolutePath();
        Log.e("attachmentPath",""+attachmentPath);
        return file;
    }

    public void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    public Bitmap getScaledBitmap(String picturePath, int width, int height) {
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

    public void sendMultipartRequest(final String APIName, ArrayList<MultipartDataModel> data){

        Log.e("multipart","Send calling");

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("Time'em");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        //pDialog.show();

        String url = context.getResources().getString(R.string.baseUrl)+APIName;
        for(int i=0;i<data.size();i++) {
            Log.e("Req Data", data.get(i).key +":"+ data.get(i).value);
        }
        MultipartRequest mCustomRequest = new MultipartRequest(url, data, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

                Log.e("volley error", "::: error , "+error.getMessage());
                Utils.showToast(context, "Something went wrong, "+error.getMessage());
            }
        }, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("volley","uploaded successfully "+" "+response.statusCode+ " "+response);


                try {
                    String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.e("volley response", ":::"+responseString);

                   // pDialog.dismiss();
                    if(response.statusCode==200) {
                        Utils.showToast(context, "File uploaded successfully.");

                      /*  if (APIName.contains("AddUpdateUserTaskActivity")) {
                            Utils.alertMessage(context, "Task Added successfully.");
                        } else if (APIName.contains("AddNotification")) {
                            Utils.alertMessage(context, " Add Notification successfully.");
                        } else {
                            Utils.alertMessage(context, " Data Uploaded successfully.");
                        }*/
                    }
                    else{
                        Utils.showToast(context, "Something went wrong,Try again");

                    }

                }catch (Exception e){
                    e.printStackTrace();
                    //pDialog.dismiss();
                    Utils.showToast(context, "Something went wrong, "+e.getMessage());
                    Log.e("volley response", "::: error , "+e.getMessage());
                }
            }
        });

        mCustomRequest.setShouldCache(false);
        mCustomRequest.setRetryPolicy(new DefaultRetryPolicy(90 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(mCustomRequest);
    }

    public void onSelectFromGalleryResult(Intent data, ImageView imageView) {
        if (Build.VERSION.SDK_INT < 19) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            attachmentPath = cursor.getString(columnIndex);
            cursor.close();

            //File sdcard = Environment.getExternalStorageDirectory();
            //File oldFile = new File(attachmentPath,".png");
            //File newFile = new File(attachmentPath,"to.png");

            String imageFileName = "IMG_" + "" + AddEditTaskEntry.UniqueNumber;
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(attachmentPath);
            File file2 = new File(storageDir + File.separator + imageFileName + ".png");
            boolean success = file.renameTo(file2);
            // oldFile.renameTo(newFile);
            if (success)
                attachmentPath = "" + file2.getAbsolutePath();
          //  Bitmap bit= getScaledBitmap(attachmentPath, 300, 300);
            imageView.setImageBitmap(getScaledBitmap(attachmentPath, 250, 200));
        }else{
            try {
                InputStream imInputStream = context.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(imInputStream);
                attachmentPath = saveGalaryImageOnLitkat(bitmap);


                String imageFileName = "IMG_" + "" + AddEditTaskEntry.UniqueNumber;
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(attachmentPath);
                File file2 = new File(storageDir + File.separator + imageFileName + ".png");
                boolean success = file.renameTo(file2);
                // oldFile.renameTo(newFile);
                if (success)
                    attachmentPath = "" + file2.getAbsolutePath();
                    imageView.setImageBitmap(getScaledBitmap(attachmentPath, 250, 200));

                //encodeImage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    private String saveGalaryImageOnLitkat(Bitmap bitmap) {
         File temp_path;
         final int COMPRESS = 100;
        try {
            File cacheDir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                cacheDir = new File(Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));
            else
                cacheDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!cacheDir.exists())
                cacheDir.mkdirs();
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(cacheDir, filename);
            temp_path = file.getAbsoluteFile();
            // if(!file.exists())
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS, out);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void onCaptureImageResult(Intent data, ImageView imageView) {
        try {
          Log.e("image path:",""+attachmentPath);
          imageView.setImageBitmap(getScaledBitmap(attachmentPath, 250, 200));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRecordVideoResult(Activity Activity,Intent data, JCVideoPlayerStandard uploadedVideo) {
        Log.e("video path:",""+attachmentPath);
      // Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(attachmentPath), 500,500);
        try {
            /*mVideoView.setVideoPath(attachmentPath);
            mVideoView.setMediaController(new MediaController(Activity));
            mVideoView.requestFocus();
            mVideoView.seekTo(15);*/
            uploadedVideo.setUp(attachmentPath , "video");

            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUniqueNumber()
    {
        String UniqueNumber="";
        long timeStamp = System.currentTimeMillis();
        UniqueNumber= HomeActivity.user.getId()+""+timeStamp;
        return UniqueNumber;
        }

}
