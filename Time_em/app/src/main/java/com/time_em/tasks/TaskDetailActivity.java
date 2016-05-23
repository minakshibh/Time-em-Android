package com.time_em.tasks;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.time_em.android.R;
import com.time_em.model.TaskEntry;
import com.time_em.utils.Utils;

public class TaskDetailActivity extends Activity {

    private ImageView back, attachment;
    private TextView info, taskDesc, taskComments, hoursWorked, txtDate;
    private TaskEntry taskEntry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initScreen();
        setClickListeners();
    }

    private void initScreen(){
        txtDate = (TextView) findViewById(R.id.date);
        info = (TextView) findViewById(R.id.info);
        taskDesc = (TextView) findViewById(R.id.taskDesc);
        taskComments = (TextView) findViewById(R.id.taskComments);
        hoursWorked = (TextView) findViewById(R.id.hoursWorked);
        back = (ImageView) findViewById(R.id.back);
        attachment = (ImageView) findViewById(R.id.attachment);
        taskEntry = (TaskEntry) getIntent().getParcelableExtra("taskEntry");

        info.setText("Task Details");
        String date = taskEntry.getCreatedDate();
//        if(date.contains(" "))
//            date = date.split(" ")[0];

        txtDate.setText(date);
        taskDesc.setText(taskEntry.getTaskName());
        taskComments.setText(taskEntry.getComments());
        hoursWorked.setText(String.valueOf(taskEntry.getSignedInHours()));
    }

    private void setClickListeners(){
        back.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v == back){
                finish();
            }
        }
    };
}
