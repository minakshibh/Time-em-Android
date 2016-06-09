package com.time_em.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.time_em.ImageLoader.ImageLoader;
import com.time_em.android.R;
import com.time_em.model.TaskEntry;
import com.time_em.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDetailActivity extends Activity {

    private ImageView back, attachment;
    private TextView info, taskDesc, taskComments, hoursWorked, txtDate;
    private TaskEntry taskEntry;
    private TextView AttachementTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initScreen();
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

        String image_url = taskEntry.getAttachmentImageFile();
        int loader = R.drawable.add;
        // ImageLoader class instance
        if (image_url != null) {
            attachment.setVisibility(View.VISIBLE);
            AttachementTxt.setVisibility(View.VISIBLE);
            ImageLoader imgLoader = new ImageLoader(getApplicationContext());
            imgLoader.DisplayImage(image_url, loader, attachment);
        }
        hoursWorked.setText(String.valueOf(taskEntry.getTimeSpent()));
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
