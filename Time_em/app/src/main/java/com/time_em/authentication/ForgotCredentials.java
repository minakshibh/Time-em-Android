package com.time_em.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

import java.util.HashMap;

public class ForgotCredentials extends Activity implements AsyncResponseTimeEm {

	private EditText loginId;
	private TextView info;
	private Time_emJsonParser parser;
	private ImageView back;
	private Button submit;
	private String trigger;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_credentials);
        
        initScreen();
        setClickListeners();
    }
    
	private void initScreen() {
		// TODO Auto-generated method stub
		loginId = (EditText)findViewById(R.id.loginId);
		info = (TextView)findViewById(R.id.info);
		back = (ImageView)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.submit);
		parser = new Time_emJsonParser(ForgotCredentials.this);

		trigger = getIntent().getStringExtra("trigger");
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		submit.setOnClickListener(listener);
		back.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == submit){
				if(loginId.getText().toString().trim().equals(""))
					Utils.showToast(ForgotCredentials.this, "Please enter login id");
				else
					submit();
			}else if(v == back){
				finish();
			}
		}
	};
	
	private void submit() {
		// TODO Auto-generated method stub
		if (Utils.isNetworkAvailable(ForgotCredentials.this)) {
			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			String _loginId = loginId.getText().toString().trim();
			String methodName;

			if(trigger.equals("password"))
				methodName = Utils.forgotPasswordAPI;
			else
				methodName = Utils.forgotPinAPI;

			postDataParameters.put("email", _loginId);
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					ForgotCredentials.this, "post", methodName,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) ForgotCredentials.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(ForgotCredentials.this, Utils.network_error);
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output", output);
		
		Utils.alertMessage(ForgotCredentials.this, "Method name: "+methodName +" ,Output: "+output);
		/*HomeActivity.user = parser.getUserDetails(output , methodName);
		 if(HomeActivity.user != null){
			 Intent intent = new Intent(ForgotCredentials.this, HomeActivity.class);
			 intent.putExtra("trigger", "login");
			 startActivity(intent);
			 finish();
		 }*/
		 
//		 Utils.alertMessage(LoginActivity.this, "Data fetched for "+user.getFirstName()+" "+user.getLastName());
//		Utils.showToast(LoginActivity.this, output);
		
		// response: 
		
//	{"Id":2,"LoginId":"admin","FirstName":"admin","LastName":"admin",
		//"FullName":"admin admin","Password":"c185ddac8b5a8f5aa23c5b80bc12d214",
		//"LoginCode":"","Supervisor":null,"SupervisorId":0,"UserType":null,
		//"UserTypeId":2,"Department":null,"DepartmentId":0,"Company":null,"CompanyId":2,
		//"Worksite":null,"WorksiteId":0,"Project":null,"ProjectId":0,"RefrenceCount":false,
		//"IsSecurityPin":null,"NFCTagId":null,"Token":"azcx2b5lwos","isError":false,"ReturnMessage":null}
	}
}
