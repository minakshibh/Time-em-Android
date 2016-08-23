package com.time_em.authentication;

import java.util.HashMap;

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
import android.widget.TextView;

import com.google.gson.Gson;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.PrefUtils;
import com.time_em.utils.Utils;

public class LoginActivity extends Activity implements AsyncResponseTimeEm {

	private EditText loginId, password;
	private Button login;
	private Time_emJsonParser parser;
	private TextView forgotPassword;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initScreen();
        setClickListeners();
		keyBoard_DoneButton();
    }
    
	private void initScreen() {
		// TODO Auto-generated method stub
		loginId = (EditText)findViewById(R.id.loginId);
		password = (EditText)findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		forgotPassword = (TextView)findViewById(R.id.forgotPassword);

		parser = new Time_emJsonParser(LoginActivity.this);
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		login.setOnClickListener(listener);
		forgotPassword.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == login){
				if(loginId.getText().toString().trim().equals("") || password.getText().toString().trim().equals(""))
					Utils.showToast(LoginActivity.this, "Please enter required information");
				else
					login();
			}else if(v == forgotPassword){
				Intent intent = new Intent(LoginActivity.this, ForgotCredentials.class);
				intent.putExtra("trigger","password");
				startActivity(intent);
			}
		}
	};
	
	private void login() {
		// TODO Auto-generated method stub
		if (Utils.isNetworkAvailable(LoginActivity.this)) {

			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			String _loginId = loginId.getText().toString().trim();
			String _password = password.getText().toString().trim();
//			=admin&=training
			postDataParameters.put("loginId", _loginId);
			postDataParameters.put("password", _password);
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					LoginActivity.this, "get", Utils.loginAPI,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) LoginActivity.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(LoginActivity.this, Utils.network_error);
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output",""+ output);
		
//		Utils.alertMessage(LoginActivity.this, output);
		HomeActivity.user = parser.getUserDetails(output , methodName);
		 if(HomeActivity.user != null){

			 //saved userId into SharedPrefs
			 Utils.saveInSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER_ID,""+HomeActivity.user.getId());
			 Gson gson = new Gson();
			 String json = gson.toJson(HomeActivity.user);
			 Utils.saveInSharedPrefs(getApplicationContext(), "user" , json );

			 Intent intent = new Intent(LoginActivity.this, CompanyListActivity.class);
			 intent.putExtra("trigger", "login");
			 startActivity(intent);
			 finish();
		 }
		 
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
	private void  keyBoard_DoneButton()
	{
		password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					hideKeyboard(LoginActivity.this);
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
