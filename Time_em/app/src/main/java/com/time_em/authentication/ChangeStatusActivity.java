package com.time_em.authentication;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChangeStatusActivity extends Activity implements AsyncResponseTimeEm{

	//todo widget
	private TextView greetings, information;
	private TextView changeStatus;
	private ImageView cancel;

	//todo classes
	private Time_emJsonParser parser;
	private Resources res;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   	requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_change_status);
	        
	    LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        
	    initScreen();
	    setClickListeners();
	}
	  
	private void initScreen() {
		// TODO Auto-generated method stub
		greetings = (TextView)findViewById(R.id.greetings);
		information = (TextView)findViewById(R.id.information);
		changeStatus = (TextView)findViewById(R.id.changeStatus);
		cancel = (ImageView)findViewById(R.id.close);
		
		parser = new Time_emJsonParser(ChangeStatusActivity.this);
		
		greetings.setText("Hello "+HomeActivity.user.getFirstName()+" "+HomeActivity.user.getLastName());
		res = ChangeStatusActivity.this.getResources();
		updateUI();
	}

	private void
	updateUI(){
		if(HomeActivity.user.isSignedIn()){
			information.setText(res.getString(R.string.userSignedIn));
			changeStatus.setText("Sign Out");
		}else{	
			information.setText(res.getString(R.string.userSignedOut));
			changeStatus.setText("Sign In");
		}
	}
	
	private void setClickListeners() {
		// TODO Auto-generated method stub
		changeStatus.setOnClickListener(listener);
		cancel.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == changeStatus){
				if(HomeActivity.user.isSignedIn()) {
					Utils.ChangeStatus(ChangeStatusActivity.this, "" + HomeActivity.user.getId(),"signOut");
				}else{
					Utils.ChangeStatus(ChangeStatusActivity.this, "" + HomeActivity.user.getId(),"signIn");
				}
			}else if (v == cancel){
				finish();
			}
		}
	};

	

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Boolean isError;
		String message;
		Log.e("output", output);
		try {
			JSONObject jObject = new JSONObject(output);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
			if(!isError){
				if(output.contains("SignedOutUser")) {
					HomeActivity.user.setSignedIn(false);
				}
				else if(output.contains("SignedinUser")){
					HomeActivity.user.setSignedIn(true);
					JSONArray jArray = jObject.getJSONArray("SignedinUser");
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject taskObject = jArray.getJSONObject(i);
						int	activeId = taskObject.getInt("Id");
						HomeActivity.user.setActivityId(activeId);
						Log.e("activeId","activeId="+activeId);
					}
				}
				else if(message.contains("already Signed out")){
					HomeActivity.user.setSignedIn(false);
				}
				else if(message.contains("already Signed In")){
					HomeActivity.user.setSignedIn(true);
				}
				updateUI();
				finish();
			}
		}catch (Exception e)
		{
			e.printStackTrace();

			}

	}
}