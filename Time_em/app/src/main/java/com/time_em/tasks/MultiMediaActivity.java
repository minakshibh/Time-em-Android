//package com.time_em.tasks;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FilterOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.MediaRecorder.VideoSource;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.text.SpannableString;
//import android.text.style.UnderlineSpan;
//import android.util.Base64;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.MediaController;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//import com.qrpatrol.activity.DashboardActivity;
//import com.qrpatrol.android.R;
//import com.qrpatrol.modal.CheckPoint;
//import com.qrpatrol.modal.Officer;
//import com.qrpatrol.util.CheckPointSelection;
//import com.qrpatrol.util.CoveredCheckPointDialog;
//import com.qrpatrol.util.QRParser;
//import com.qrpatrol.util.Util;
//import com.time_em.utils.Config;
//
//public class MultiMediaActivity extends Activity implements OnClickListener,
//		CheckPointSelection, SurfaceHolder.Callback {
//	// LogCat tag
//	private static final String TAG = MultiMediaActivity.class.getSimpleName();
//	ProgressDialog pDialog = null;
//	TextView record_video;
//	// Camera activity request codes
//	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
//	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
//	private static final int SELECT_AUDIO_FILE_PATH = 300;
//
//	public static final int MEDIA_TYPE_IMAGE = 1;
//	public static final int MEDIA_TYPE_VIDEO = 2;
//	private String response;
//	private SharedPreferences patrolPrefs;
//
//	private Uri fileUri;
//	private String filepath = "", audio_path = "";
//	LinearLayout upload_videos, assignedCP;
//	private String upLoadServerUri = "http://app.mysecurityguards.com/report-mme.php";
//	long totalSize = 0;
//
//	ImageView imageView1, menu;
//	private CheckPoint checkPoint;
//
//	private String patrolID, notes_str, en_notes_str, encodedString;
//	EditText text_notes;
//	private int setimgeui = 0;
//	private TextView tap_here, record_video1;
//	byte[] byteArray;
//	// private VideoView video_view;
//	DisplayMetrics dm;
//	String scannedCheckPoint = "0";
//	private QRParser qrParser;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_multimedia);
//		checkPoint = (CheckPoint) getIntent().getParcelableExtra("checkpoint");
//
//		patrolID = getIntent().getStringExtra("patrolID");
//		SetUI();
//
//	}
//
//	private void SetUI() {
//		// TODO Auto-generated method stub
//		qrParser = new QRParser(MultiMediaActivity.this);
//		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
//
//		record_video = (TextView) findViewById(R.id.record_video);
//		upload_videos = (LinearLayout) findViewById(R.id.upload_videos);
//		imageView1 = (ImageView) findViewById(R.id.desc);
//		menu = (ImageView) findViewById(R.id.menu);
//		menu.setVisibility(View.GONE);
//		text_notes = (EditText) findViewById(R.id.text_notes);
//		// progressBar = (ProgressBar) findViewById(R.id.progressBar);
//		record_video1 = (TextView) findViewById(R.id.record_video1);
//
//		tap_here = (TextView) findViewById(R.id.tap_here);
//
//		SpannableString content = new SpannableString(
//				"Tap here for click your pic.");
//		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//		tap_here.setText(content);
//
//		SpannableString content1 = new SpannableString("Record Video");
//		content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
//
//		record_video.setText(content1);
//		assignedCP = (LinearLayout) findViewById(R.id.assignCheckPoint);
//		tap_here.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				// clickImage();
//				clickImage();
//
//			}
//		});
//		record_video.setOnClickListener(this);
//		upload_videos.setOnClickListener(this);
//		assignedCP.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.record_video:
//			recordVideo();
//
//			break;
//
//		case R.id.assignCheckPoint:
//			CoveredCheckPointDialog dialog = new CoveredCheckPointDialog(
//					MultiMediaActivity.this,
//					"Please scan some CheckPoint before reporting MME.");
//			dialog.show();
//
//			break;
//
//		case R.id.upload_videos:
//			// new UploadFileToServer().execute();
//
//			if (Util.isNetworkAvailable(MultiMediaActivity.this)) {
//				if (filepath == null)
//					filepath = "";
//				if (audio_path == null)
//					audio_path = "";
//
//				try {
//
//					notes_str = text_notes.getText().toString().trim();
//					if (filepath.equals("") && audio_path.equals("")
//							&& notes_str.equals("")) {
//						Util.alertMessage(MultiMediaActivity.this,
//								"Please fill atleast one field. Either Record Video, Capture Image or Enter Notes.");
//					} else {
//						// if (notes_str.length() > 0) {
//						// if (filepath != null && !filepath.isEmpty()) {
//						if (!scannedCheckPoint.equals("0")) {
//							try {
//								en_notes_str = URLDecoder.decode(notes_str,
//										"UTF-8");
//								new GetData().execute();
//
//							} catch (UnsupportedEncodingException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						} else {
//							Toast.makeText(MultiMediaActivity.this,
//									"Please assign some checkpoint.",
//									Toast.LENGTH_LONG).show();
//						}
//
//						// }else{
//						// Toast.makeText(MultiMediaActivity.this,
//						// "Please upload video", Toast.LENGTH_LONG).show();
//						// }
//
//						// } else {
//						// Toast.makeText(MultiMediaActivity.this,
//						// "Please enter notes.", Toast.LENGTH_LONG).show();
//						// }
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					String stackTrace = Log.getStackTraceString(e);
//					Util.alertMessage(MultiMediaActivity.this, stackTrace);
//				}
//			} else {
//				Util.alertMessage(MultiMediaActivity.this,
//						"Please check your internet connection");
//			}
//		}
//	}
//
//	/**
//	 * Launching camera app to record video
//	 */
//	private void clickImage() {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//		// start the image capture Intent
//		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//	}
//
//	/*
//	 * Recording video
//	 */
//	private void recordVideo() {
//		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//		fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//
//		// set video quality
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
//															// name
//
//		// start the video capture Intent
//		startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
//	}
//
//
//
//	/**
//	 * returning image / video
//	 */
//	private static File getOutputMediaFile(int type) {
//
//		// External sdcard location
//		File mediaStorageDir = new File(
//				Environment
//						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//				Config.IMAGE_DIRECTORY_NAME);
//
//		// Create the storage directory if it does not exist
//		if (!mediaStorageDir.exists()) {
//			if (!mediaStorageDir.mkdirs()) {
//				Log.d(TAG, "Oops! Failed create " + Config.IMAGE_DIRECTORY_NAME
//						+ " directory");
//				return null;
//			}
//		}
//
//		// Create a media file name
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//				Locale.getDefault()).format(new Date());
//		File mediaFile;
//		if (type == MEDIA_TYPE_IMAGE) {
//			mediaFile = new File(mediaStorageDir.getPath() + File.separator
//					+ "IMG_" + timeStamp + ".jpg");
//		} else if (type == MEDIA_TYPE_VIDEO) {
//			mediaFile = new File(mediaStorageDir.getPath() + File.separator
//					+ "VID_" + timeStamp + ".mp4");
//		} else {
//			return null;
//		}
//
//		return mediaFile;
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//
//		// save file url in bundle as it will be null on screen orientation
//		// changes
//		outState.putParcelable("file_uri", fileUri);
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//
//		// get the file url
//		fileUri = savedInstanceState.getParcelable("file_uri");
//	}
//
//	/**
//	 * Receiving activity result method will be called after closing the camera
//	 * */
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// if the result is capturing Image
//		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
//			if (resultCode == RESULT_OK) {
//
//				// successfully captured the image
//				// launching upload activity
//
//				// bimatp factory
//				BitmapFactory.Options options = new BitmapFactory.Options();
//
//				// downsizing image as it throws OutOfMemory Exception for
//				// larger
//				// images
//				options.inSampleSize = 8;
//				audio_path = fileUri.getPath();
//
//				final Bitmap bitmap = BitmapFactory.decodeFile(audio_path,
//						options);
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//				byteArray = stream.toByteArray();
//				// Encode Image to String
//				encodedString = Base64.encodeToString(byteArray, 0);
//				Log.d("", "encodedString" + encodedString);
//				setimgeui = 1;
//				imageView1.setVisibility(View.VISIBLE);
//				imageView1.setImageBitmap(ExifUtils.rotateBitmap(
//						fileUri.getPath(), bitmap));
//
//			} else if (resultCode == RESULT_CANCELED) {
//
//				// user cancelled Image capture
//				Toast.makeText(getApplicationContext(),
//						"User cancelled image capture", Toast.LENGTH_SHORT)
//						.show();
//
//			} else {
//				// failed to capture image
//				Toast.makeText(getApplicationContext(),
//						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
//						.show();
//			}
//
//		} else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
//			if (resultCode == RESULT_OK) {
//
//				// video successfully recorded
//				// launching upload activity
//				filepath = fileUri.getPath();
//				Uri uri = Uri.parse(filepath);
//				record_video1.setText(filepath);
//			} else if (resultCode == RESULT_CANCELED) {
//
//				// user cancelled recording
//				Toast.makeText(getApplicationContext(),
//						"User cancelled video recording", Toast.LENGTH_SHORT)
//						.show();
//
//			} else {
//				// failed to record video
//				Toast.makeText(getApplicationContext(),
//						"Sorry! Failed to record video", Toast.LENGTH_SHORT)
//						.show();
//			}
//		} else if (requestCode == SELECT_AUDIO_FILE_PATH) {
//
//			if (resultCode == RESULT_OK) {
//
//				// video successfully recorded
//				// launching upload activity
//				if (requestCode == 1 && resultCode == RESULT_OK) {
//					Uri selectedImageUri = data.getData();
//					audio_path = getPath(selectedImageUri);
//					Log.d("", "audio_path" + audio_path);
//					Bitmap bitmap = BitmapFactory.decodeFile(audio_path);
//
//				}
//
//			} else if (resultCode == RESULT_CANCELED) {
//
//				// user cancelled recording
//				Toast.makeText(getApplicationContext(),
//						"User cancelled video recording", Toast.LENGTH_SHORT)
//						.show();
//
//			} else {
//				// failed to record video
//				Toast.makeText(getApplicationContext(),
//						"Sorry! Failed to record video", Toast.LENGTH_SHORT)
//						.show();
//			}
//
//		}
//	}
//
//	private class GetData extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// Showing progress dialog
//			pDialog = new ProgressDialog(MultiMediaActivity.this);
//			pDialog.show();
//			pDialog.setCancelable(false);
//
//			pDialog.setContentView(R.layout.progressdialog);
//
//		}
//
//		@SuppressWarnings("deprecation")
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			response = uploadFileToserver();
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			// Dismiss the progress dialog
//			pDialog.dismiss();
//			String res = qrParser.getEventResponse(response);
//			if (!res.equals("failure")) {
//
//				Editor editor = patrolPrefs.edit();
//				editor.putString("eventName", "MME");
//				editor.commit();
//
//				Util.showToast(MultiMediaActivity.this,
//						"Event submitted successfully.");
//				finish();
//			}
//
//		}
//
//	}
//
//	@SuppressWarnings("deprecation")
//	public String getPath(Uri uri) {
//		String[] projection = { MediaStore.Images.Media.DATA };
//		Cursor cursor = managedQuery(uri, projection, null, null, null);
//		int column_index = cursor
//				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//		cursor.moveToFirst();
//		return cursor.getString(column_index);
//	}
//
//	/**
//	 * Method to show alert dialog
//	 * */
//	private void showAlert(String message) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(message).setTitle("Response from Servers")
//				.setCancelable(false)
//				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						// do nothing
//					}
//				});
//		AlertDialog alert = builder.create();
//		alert.show();
//	}
//
//	@SuppressWarnings("deprecation")
//	public String uploadFileToserver() {
//		FileBody videoBody, imgBody;
//
//		String serverResponse = "";
//		try {
//
////			long fileLength = file.length();
////			Log.d("", "fileLength " + fileLength);
//
//			HttpClient client = new DefaultHttpClient();
//
//			HttpPost post = new HttpPost(upLoadServerUri);
//
//			@SuppressWarnings("deprecation")
//			MultipartEntity reqEntity = new MultipartEntity(
//					HttpMultipartMode.BROWSER_COMPATIBLE);
//
//			if(!filepath.equals("")){
//
//				File file = new File(filepath);
//				 videoBody = new FileBody(file);
//				 reqEntity.addPart("video", videoBody);
//			}
//
//			if(!audio_path.equals("")){
//				File file1 = new File(audio_path);
//				imgBody =  new FileBody(file1);
//				reqEntity.addPart("img",imgBody);
//			}
//
//			reqEntity.addPart("patrol_id", new StringBody(patrolID));
//			reqEntity.addPart("officer_id", new StringBody(
//					DashboardActivity.officer.getOfficerId()));
//			reqEntity.addPart("shift_id", new StringBody(
//					DashboardActivity.officer.getShiftId()));
//
//			reqEntity.addPart("event_name", new StringBody("MME"));
//			reqEntity.addPart("latitude",
//					new StringBody(String.valueOf(DashboardActivity.myLat)));
//			reqEntity.addPart("longitude",
//					new StringBody(String.valueOf(DashboardActivity.myLon)));
//			reqEntity.addPart("checkpoint_id",
//					new StringBody(scannedCheckPoint));
//			reqEntity.addPart("text", new StringBody(en_notes_str));
//			post.setEntity(reqEntity);
//			HttpResponse response = client.execute(post);
//			HttpEntity resEntity = response.getEntity();
//
//			if (resEntity != null) {
//				serverResponse = EntityUtils.toString(resEntity);
//				Log.i("RESPONSE", serverResponse);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return serverResponse;
//	}
//
//	public static String getContent(HttpResponse response) throws IOException {
//		BufferedReader rd = new BufferedReader(new InputStreamReader(response
//				.getEntity().getContent()));
//		String body = "";
//		String content = "";
//
//		while ((body = rd.readLine()) != null) {
//			content += body + "\n";
//		}
//		return content.trim();
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void OnCheckPointSelected(CheckPoint checkPoint) {
//		// TODO Auto-generated method stub
//		scannedCheckPoint = checkPoint.getCheckPointId();
//	}
//}
