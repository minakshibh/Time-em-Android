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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.time_em.android.R;
import com.time_em.model.MultipartDataModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by minakshi on 08/06/16.
 */
public class FileUtils {
    private String userChoosenTask, attachmentPath;
    private Context context;
    public static int SELECT_FILE = 1, REQUEST_CAMERA = 2;

    public FileUtils(Context context){
        this.context = context;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void showChooserDialog() {


        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
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
                } else if (items[item].equals("Cancel")) {
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
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                ((Activity)context).startActivityForResult(takePictureIntent, REQUEST_CAMERA);
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
    public void sendMultipartRequest(String APIName, ArrayList<MultipartDataModel> data){

        Log.e("Notification","Send notification called");

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setTitle("Time'em");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = context.getResources().getString(R.string.baseUrl)+APIName;

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
                Log.e("volley","uploaded successfully");

                try {
                    String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    Log.e("volley response", ":::"+responseString);

                    pDialog.dismiss();
                    Utils.alertMessage(context, "::::: " + responseString);

                }catch (Exception e){
                    e.printStackTrace();
                    pDialog.dismiss();
                    Utils.showToast(context, "Something went wrong, "+e.getMessage());
                    Log.e("volley response", "::: error , "+e.getMessage());
                }
            }
        });

        mCustomRequest.setShouldCache(false);
        mCustomRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(mCustomRequest);
    }

    public void onSelectFromGalleryResult(Intent data, ImageView imageView) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        attachmentPath = cursor.getString(columnIndex);
        cursor.close();
        imageView.setImageBitmap(getScaledBitmap(attachmentPath, 800, 800));

    }

    public void onCaptureImageResult(Intent data, ImageView imageView) {
        try {
            imageView.setImageBitmap(getScaledBitmap(attachmentPath, 800, 800));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
