package com.time_em.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.time_em.ImageLoader.ImageLoader;
import com.time_em.android.R;
import com.time_em.model.TaskEntry;
import com.time_em.utils.FileUtils;
import com.time_em.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class TaskDetailActivity extends Activity {

    private ImageView back, attachment;
    private TextView info, taskDesc, taskComments, hoursWorked, txtDate;
    private TaskEntry taskEntry;
    private TextView AttachementTxt;
    private VideoView videoView;
    private String image_url=null;
    //JCVideoPlayerStandard videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initScreen();
        fetchImageData();
        setClickListeners();
    }



    private void initScreen() {
        txtDate = (TextView) findViewById(R.id.date);
        info = (TextView) findViewById(R.id.info);
        taskDesc = (TextView) findViewById(R.id.taskDesc);
        taskComments = (TextView) findViewById(R.id.taskComments);
        hoursWorked = (TextView) findViewById(R.id.hoursWorked);
        back = (ImageView) findViewById(R.id.back);
        attachment = (ImageView) findViewById(R.id.attachment);
        AttachementTxt = (TextView) findViewById(R.id.AttachementTxt);
        attachment.setVisibility(View.GONE);
        AttachementTxt.setVisibility(View.GONE);
        videoView = (VideoView) findViewById(R.id.videoView);
       // videoView=(VideoView)findViewById(R.id.videoView);
        //mVideoPlayer_2 = (VideoView)root.findViewById(R.id.videoView);
        taskEntry = (TaskEntry) getIntent().getParcelableExtra("taskEntry");
        taskComments.setMovementMethod(new ScrollingMovementMethod());
        info.setText("Task Details");
        String date = taskEntry.getCreatedDate();

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date newDate = format.parse(date);
            format = new SimpleDateFormat("EEE dd MMM,yyyy");
            String datestr = format.format(newDate);
            txtDate.setText(datestr);
        } catch (Exception e) {

        }

        taskDesc.setText(taskEntry.getTaskName());
        taskComments.setText(taskEntry.getComments());
        hoursWorked.setText(String.valueOf(taskEntry.getTimeSpent()));


    }
    private void fetchImageData() {
        //image_url= "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        image_url= taskEntry.getAttachmentImageFile();
        Log.e("image_url",""+image_url);
        if(image_url!=null && !image_url.equalsIgnoreCase("null"))
        {
            AttachementTxt.setVisibility(View.VISIBLE);

            if(image_url.contains("http")) {
                if (image_url.contains(".png") | image_url.contains(".jpg")) {
                    attachment.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    int loader = R.drawable.add;
                    if (image_url != null) {

                        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                        imgLoader.DisplayImage(image_url, loader, attachment);
                    }
                } else {
                    videoView.setVisibility(View.VISIBLE);
                    attachment.setVisibility(View.GONE);
                    videoView.setVideoPath(image_url);
                    videoView.setMediaController(new MediaController(TaskDetailActivity.this));
                    videoView.requestFocus();
                    videoView.seekTo(5);

                }/*MediaController mc = new MediaController(this);
               *//* MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                Uri video = Uri.parse(image_url);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(video);
                videoView.start();*//*

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int h = displaymetrics.heightPixels;
                int w = displaymetrics.widthPixels;
                int left = videoView.getLeft();
                int top = videoView.getTop();
                int right = left + (w / 2);
                int botton = top + (h / 2);
                videoView.layout(left, top, right, botton);
                mc = new MediaController(this);
                mc.setEnabled(true);
                videoView.setVideoPath(image_url);
                mc.setAnchorView(videoView);
                videoView.setMediaController(mc);
                videoView.start();
                videoView.seekTo(1);*/


                //videoView.setUp(image_url, "Video");
            }
            else{
             /*   videoView.setVisibility(View.GONE);
                attachment.setVisibility(View.VISIBLE);
                FileUtils fileUtils=new FileUtils(TaskDetailActivity.this);
                attachment.setImageBitmap(fileUtils.getScaledBitmap(image_url, 800, 800));*/

                if(image_url.contains(".jpg") | image_url.contains(".png")) {
                    attachment.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    FileUtils fileUtils = new FileUtils(TaskDetailActivity.this);
                    attachment.setImageBitmap(fileUtils.getScaledBitmap(image_url, 800, 800));
                }
                else{
                    attachment.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoPath(image_url);
                    videoView.setMediaController(new MediaController(TaskDetailActivity.this));
                    videoView.requestFocus();
                    videoView.seekTo(5);
                }
            }


        }
        else{
            attachment.setVisibility(View.GONE);
            AttachementTxt.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // videoView.releaseAllVideos();
    }

    private void setClickListeners() {
        back.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == back) {
                finish();
            }
        }
    };
}
