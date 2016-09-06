package com.time_em.authentication;

import java.util.HashMap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.utils.Utils;

public class TokenRegistrationActivity extends Activity implements AsyncResponseTimeEm {

	private EditText txtToken;
	private Button register;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_registration);
        
        initScreen();
        setClickListeners();
    }
    
	private void initScreen() {
		// TODO Auto-generated method stub
		txtToken = (EditText)findViewById(R.id.txtToken);
		register = (Button)findViewById(R.id.register);
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		register.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == register){
				if(txtToken.getText().toString().trim().equals(""))
					Utils.showToast(TokenRegistrationActivity.this, "Please enter valid token");
				else
					register();
			}
		}
	};
	
	private void register() {
		// TODO Auto-generated method stub
		if (Utils.isNetworkAvailable(TokenRegistrationActivity.this)) {

			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			String token = txtToken.getText().toString().trim();
			postDataParameters.put("Token", token);
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					TokenRegistrationActivity.this, "get", Utils.tokenRegistrationAPI,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) TokenRegistrationActivity.this;
			mWebPageTask.execute();

		} else {
			Utils.alertMessage(TokenRegistrationActivity.this, Utils.network_error);
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Utils.showToast(TokenRegistrationActivity.this, output);
	}
}
