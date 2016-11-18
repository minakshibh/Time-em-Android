package com.time_em.authentication;

import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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

	//todo widgets
	private EditText loginId, password;
	private Button login;
	private TextView forgotPassword;
	private User user;

	//todo classes
	private Time_emJsonParser parser;

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
		// TODO initialing the variables and views
		loginId = (EditText)findViewById(R.id.loginId);
		password = (EditText)findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		forgotPassword = (TextView)findViewById(R.id.forgotPassword);

		parser = new Time_emJsonParser(LoginActivity.this);
	}

	private void setClickListeners() {
		// TODO set on click listener
		login.setOnClickListener(listener);
		forgotPassword.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v == login){
					if(loginId.getText().toString().trim().equals("") ) {
						Utils.alertMessageWithoutBack(LoginActivity.this,"Please enter Username");
					}else if(password.getText().toString().trim().equals("")){
						Utils.alertMessageWithoutBack(LoginActivity.this,"Please enter password");
					}
					else {
						Utils.hideKeyboard(LoginActivity.this);
						login();
					}
				}else if(v == forgotPassword){
					Intent intent = new Intent(LoginActivity.this, ForgotCredentials.class);
					intent.putExtra("trigger","password");
					startActivity(intent);
				}
		}
	};
	
	private void login() {

		// TODO log in api calling
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
			Utils.alertMessageWithoutBack(LoginActivity.this, Utils.network_error);
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output",""+ output);
		
		if(methodName.equalsIgnoreCase(Utils.loginAPI)) {
			user = parser.getUserDetails(output, methodName);
			if (user != null) {

				// Todo saved userId into SharedPrefs
				Utils.saveInSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER_ID, "" + user.getId());
				PrefUtils.setIntegerPreference(getApplicationContext(), PrefUtils.KEY_ACTIVITY_ID, user.getActivityId());
				boolean value =user.isSignedIn();
				PrefUtils.setBooleanPreference(getApplicationContext(), PrefUtils.KEY_IS_SIGNED_IN,value);

				Gson gson = new Gson();
				String json = gson.toJson(user);
				Utils.saveInSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER, json);

				Intent intent = new Intent(LoginActivity.this, CompanyListActivity.class);
				intent.putExtra("trigger", "login");
				startActivity(intent);
				finish();
			}
			else{
				//Utils.showToast(LoginActivity.this,"Something went wrong.");
			}
		}
	}

	private void  keyBoard_DoneButton()
	{
		password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Utils.hideKeyboard(LoginActivity.this);
					return true;
				}
				return false;
			}
		});
	}

}
