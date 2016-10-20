package com.time_em.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

	// todo widget
	private EditText Email;
	private TextView info,txt_enterEmail;
	private ImageView back;
	private Button submit;
	//todo variables
	private String trigger;
	//todo classes
	private Time_emJsonParser parser;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_credentials);
        
        initScreen();
        setClickListeners();
		keyBoard_DoneButton();
    }
    
	private void initScreen() {
		// TODO Auto-generated method stub
		Email = (EditText)findViewById(R.id.Email);
		info = (TextView)findViewById(R.id.info);
		txt_enterEmail=(TextView)findViewById(R.id.txt_enterEmail);
		back = (ImageView)findViewById(R.id.back);
		submit = (Button)findViewById(R.id.submit);
		parser = new Time_emJsonParser(ForgotCredentials.this);

		trigger = getIntent().getStringExtra("trigger");

		if(trigger!=null)
		{
			if(trigger.equalsIgnoreCase("password"))
			{
				info.setText("Forgot Password");
				txt_enterEmail.setText("Enter your email to reset your password.");
			}else{
				info.setText("Forgot Pin");
				txt_enterEmail.setText("Enter your email to reset your pin.");
			}
		}
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
				if(Email.getText().toString().trim().equals("")) {
					Utils.showToast(ForgotCredentials.this, "Please enter email address.");
				}
				else {
					Utils.hideKeyboard(ForgotCredentials.this);
					submit();
				}
			}else if(v == back){
				finish();
			}
		}
	};
	
	private void submit() {
		// TODO Auto-generated method stub
		if (Utils.isNetworkAvailable(ForgotCredentials.this)) {
			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			String _loginId = Email.getText().toString().trim();
			String methodName="";

			if(trigger.equals("password"))
				methodName = Utils.forgotPasswordAPI;
			else
				methodName = Utils.forgotPinAPI;

			postDataParameters.put("email", _loginId);

			Log.e(methodName,""+postDataParameters.toString());
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					ForgotCredentials.this, "post", methodName,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) ForgotCredentials.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessageWithoutBack(ForgotCredentials.this, Utils.network_error);
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output", output);
		Utils.alertMessage(ForgotCredentials.this, output);
		}
	private void  keyBoard_DoneButton()
	{
		Email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					hideKeyboard(ForgotCredentials.this);
					return true;
				}
				return false;
			}
		});
	}
	public void hideKeyboard(Context cxt) {
		//   context=cxt;
		InputMethodManager inputManager = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = ((Activity) cxt).getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
