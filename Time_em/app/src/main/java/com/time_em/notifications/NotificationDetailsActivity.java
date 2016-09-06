package com.time_em.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.time_em.ImageLoader.ImageLoader;
import com.time_em.android.R;
import com.time_em.model.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NotificationDetailsActivity extends Activity {


    private ImageView sendNotification, back;
    private String attachmentPath = null;
    private TextView headerText;
    private TextView textViewDate,textViewName,textViewSubject,textViewMassage;
    private LinearLayout lay_Attachment;
    private Notification mNotification;
    private ImageView Image_Attachment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notitification_details);

        initScreen();
        setUpClickListeners();
        getNotification();
    }



    private void initScreen() {
        sendNotification = (ImageView) findViewById(R.id.AddButton);
        sendNotification.setVisibility(View.GONE);
        back = (ImageView)findViewById(R.id.back);
        headerText = (TextView)findViewById(R.id.headerText);
        headerText.setText("Notification Details");
        lay_Attachment=(LinearLayout)findViewById(R.id.lay_Attachment);
        lay_Attachment.setVisibility(View.INVISIBLE);

        textViewDate= (TextView)findViewById(R.id.textViewDate);
        textViewName= (TextView)findViewById(R.id.textViewName);

        textViewSubject= (TextView)findViewById(R.id.textViewSubject);
        textViewMassage= (TextView)findViewById(R.id.textViewMassage);
        Image_Attachment=(ImageView)findViewById(R.id.Image_Attachment);

    }
    private void setUpClickListeners() {
        back.setOnClickListener(listener);
    }
    private void getNotification() {
        mNotification = (Notification) getIntent().getParcelableExtra("notification");

        String date = mNotification.getCreatedDate();

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date newDate = format.parse(date);
            format = new SimpleDateFormat("EEE dd MMM, yyyy hh:mm");
            String datestr = format.format(newDate);
            textViewDate.setText(datestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        textViewName.setText(mNotification.getSenderFullName());
        textViewSubject.setText(mNotification.getSubject());
        textViewMassage.setText(mNotification.getMessage());

        attachmentPath=mNotification.getAttachmentPath();
        Log.e("attachmentPath",""+attachmentPath);
        if(attachmentPath!=null && !attachmentPath.equalsIgnoreCase("null"))
        {
            lay_Attachment.setVisibility(View.VISIBLE);
            if(attachmentPath.contains("http")) {

                int loader = R.drawable.add;
                ImageLoader imgLoader = new ImageLoader(getApplicationContext());
                imgLoader.DisplayImage(attachmentPath, loader, Image_Attachment);
            }
           /* else
            {

                FileUtils fileUtils=new FileUtils(NotificationDetailsActivity.this);
                Bitmap bitmap= fileUtils.getScaledBitmap(attachmentPath,500,500);
                Image_Attachment.setImageBitmap(bitmap);
                }*/
            }
        else
        {
            lay_Attachment.setVisibility(View.INVISIBLE);
            }


    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == sendNotification){

            }else if(v == back){
                finish();
            }
        }
    };
}