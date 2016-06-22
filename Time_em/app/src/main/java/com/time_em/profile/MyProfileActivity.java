package com.time_em.profile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.time_em.ImageLoader.ImageLoader;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileActivity extends Activity implements AsyncResponseTimeEm {


    private Time_emJsonParser parser;
    private User mUser;

    private ImageView editButton, back;
    private CircleImageView circularImageView;
    private TextView headerText,txt_Logout;
    private EditText edt_Name,edt_Email,edt_pass,edt_Phone;
    private TextView txtName;

    private String Image_path=null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        initScreen();
        setUpClickListeners();
        fetchUserData();
        disableAll();

    }



    private void initScreen() {
        editButton = (ImageView) findViewById(R.id.AddButton);
        editButton.setVisibility(View.GONE);
        editButton.setImageDrawable(getResources().getDrawable(R.drawable.edit));
        back = (ImageView) findViewById(R.id.back);
        headerText=(TextView) findViewById(R.id.headerText);
        headerText.setText("Profile");
        txtName=(TextView) findViewById(R.id.txtName);
        txt_Logout=(TextView) findViewById(R.id.txt_Logout);

        edt_Name=(EditText)findViewById(R.id.edt_Name);
        edt_Email=(EditText)findViewById(R.id.edt_Email);
     //   edt_pass=(EditText)findViewById(R.id.edt_pass);
        edt_Phone=(EditText)findViewById(R.id.edt_Phone);
        circularImageView=(CircleImageView)findViewById(R.id.profile_image);

    }
    private void setUpClickListeners() {
        back.setOnClickListener(listener);
        editButton.setOnClickListener(listener);
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == back){
                finish();
            }else if(v == editButton){
                EnableAll();
            }
        }
    };
    private void fetchUserData() {
        mUser= HomeActivity.user;
        edt_Name.setText(mUser.getFullName());
        txtName.setText(mUser.getFirstName()+" "+mUser.getLastName());
        edt_Email.setText(mUser.getEmail());
      //  edt_pass.setText(mUser.getLastName());
        edt_Phone.setText(mUser.getPhoneNumber());
        Image_path="";
        int loader = R.drawable.user_profile;
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
        imgLoader.DisplayImage(Image_path, loader, circularImageView);

    }
    private void disableAll()
    {
        edt_Name.setEnabled(false);
        changeColor(edt_Name,false);

        edt_Email.setEnabled(false);
        changeColor(edt_Email,false);

       // edt_pass.setEnabled(false);
       // changeColor(edt_pass,false);

        edt_Phone.setEnabled(false);
        changeColor(edt_Phone,false);
        }
    private void EnableAll()
    {
        edt_Name.setEnabled(true);
        changeColor(edt_Name,true);

        edt_Email.setEnabled(true);
        changeColor(edt_Email,true);

       // edt_pass.setEnabled(true);
       // changeColor(edt_pass,true);

        edt_Phone.setEnabled(true);
        changeColor(edt_Phone,true);
        }
    private void changeColor(EditText edt,boolean color)
    {
        if(color){
            edt.setTextColor(getResources().getColor(R.color.black));
        }else {
            edt.setTextColor(getResources().getColor(R.color.grey));
        }
    }
    @Override
    public void processFinish(String output, String methodName) {

    }

}