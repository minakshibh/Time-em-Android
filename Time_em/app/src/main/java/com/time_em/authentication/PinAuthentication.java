package com.time_em.authentication;

/*Client ID: 	381130279932.apps.googleusercontent.com
Redirect URIs: 	urn:ietf:wg:oauth:2.0:oob http://localhost
Application type: 	Android
Package name: 	com.ultimasquare.pinview
Certificate fingerprint (SHA1): 	86:F2:4D:FD:34:98:BF:0C:47:94:34:D4:8C:68:A3:84:B7:D7:B2:0F
Deep Linking: 	Disabled*/


import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
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

public class PinAuthentication extends Activity implements AsyncResponseTimeEm {


	//todo widgets
	private TextView pinBox0, pinBox1, pinBox2, pinBox3, forgotPin,login;
	private TextView [] pinBoxArray;
	private LinearLayout btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDelete;
	//todo variables
	private String userEntered;
	private final int PIN_LENGTH = 4;
	private User user;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userEntered = "";

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pin_authentication);
		
		initScreen();
		setClickListeners();
	}

	private void initScreen(){
		  btn0 = (LinearLayout)findViewById(R.id.button0);
		  btn1 = (LinearLayout)findViewById(R.id.button1);
		  btn2 = (LinearLayout)findViewById(R.id.button2);
		  btn3 = (LinearLayout)findViewById(R.id.button3);
		  btn4 = (LinearLayout)findViewById(R.id.button4);
		  btn5 = (LinearLayout)findViewById(R.id.button5);
		  btn6 = (LinearLayout)findViewById(R.id.button6);
		  btn7 = (LinearLayout)findViewById(R.id.button7);
		  btn8 = (LinearLayout)findViewById(R.id.button8);
		  btn9 = (LinearLayout)findViewById(R.id.button9);
		  btnDelete = (LinearLayout)findViewById(R.id.buttonDeleteBack);
		  
		  pinBox0 = (TextView)findViewById(R.id.pinBox0);
		  pinBox1 = (TextView)findViewById(R.id.pinBox1);
		  pinBox2 = (TextView)findViewById(R.id.pinBox2);
		  pinBox3 = (TextView)findViewById(R.id.pinBox3);
		  forgotPin = (TextView)findViewById(R.id.forgotPin);
		  login= (TextView)findViewById(R.id.login);

		   btn0.setTag("0");
		  btn1.setTag("1");
		  btn2.setTag("2");
		  btn3.setTag("3");
		  btn4.setTag("4");
		  btn5.setTag("5");
		  btn6.setTag("6");
		  btn7.setTag("7");
		  btn8.setTag("8");
		  btn9.setTag("9");
		  
		  pinBoxArray = new TextView[PIN_LENGTH];
			pinBoxArray[0] = pinBox0;
			pinBoxArray[1] = pinBox1;
			pinBoxArray[2] = pinBox2;
			pinBoxArray[3] = pinBox3;
	}
	
	private void setClickListeners(){
		  btn0.setOnClickListener(pinButtonHandler);
		  btn1.setOnClickListener(pinButtonHandler);
		  btn2.setOnClickListener(pinButtonHandler);
		  btn3.setOnClickListener(pinButtonHandler);
		  btn4.setOnClickListener(pinButtonHandler);
		  btn5.setOnClickListener(pinButtonHandler);
		  btn6.setOnClickListener(pinButtonHandler);
		  btn7.setOnClickListener(pinButtonHandler);
		  btn8.setOnClickListener(pinButtonHandler);
		  btn9.setOnClickListener(pinButtonHandler);
		  btnDelete.setOnClickListener(pinButtonHandler);
		  forgotPin.setOnClickListener(pinButtonHandler);
		login.setOnClickListener(pinButtonHandler);
	}
	
	View.OnClickListener pinButtonHandler = new View.OnClickListener() {
	    public void onClick(View v) {

	    	if(v== btnDelete){
	    		
		    	if (userEntered.length()>0)
		    	{
		    		userEntered = userEntered.substring(0,userEntered.length()-1);
		    		pinBoxArray[userEntered.length()].setText("");
		    	}
	    	}else if(v == forgotPin){
				Intent intent = new Intent(PinAuthentication.this, ForgotCredentials.class);
				intent.putExtra("trigger","pin");
				startActivity(intent);

			}
			else if(v==login)
			{
				Intent intent = new Intent(PinAuthentication.this, LoginActivity.class);
				startActivity(intent);
				finish();
				}


			else{
	    	
	    	LinearLayout pressedButton = (LinearLayout)v;
    		
	    	
	    	if (userEntered.length()<PIN_LENGTH)
	    	{
	    		userEntered = userEntered + pressedButton.getTag().toString();
	    		Log.v("PinView", "User entered="+userEntered);
	    		
	    		//Update pin boxes
	    		pinBoxArray[userEntered.length()-1].setText(pressedButton.getTag().toString());
	    		
	    		if (userEntered.length()==PIN_LENGTH)
	    		{
	    			//Check if entered PIN is correct
	    			authenticate(userEntered);
	    		}	
	    	}
	    	else
	    	{
	    		//Roll over
	    		pinBoxArray[0].setText("");
	    		pinBoxArray[1].setText("");
	    		pinBoxArray[2].setText("");
	    		pinBoxArray[3].setText("");
	    		
	    		userEntered = "";
	    		
	    		userEntered = userEntered + pressedButton.getTag().toString();
	    		Log.v("PinView", "User entered="+userEntered);
	    		
	    		pinBoxArray[userEntered.length()-1].setText("8");
	    		
	    	}
	    	}
	    	
	    }
	  };
	  
	private void authenticate(String pin){
		if (Utils.isNetworkAvailable(PinAuthentication.this)) {
			HashMap<String, String> postDataParameters = new HashMap<String, String>();
			
			String _pin = pin;
			String _loginId = Utils.getSharedPrefs(PinAuthentication.this, "loginId");
			
			postDataParameters.put("loginId", _loginId);
			postDataParameters.put("SecurityPin", _pin);
			
			AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
					PinAuthentication.this, "get", Utils.pinAuthenticationAPI,
					postDataParameters, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseTimeEm) PinAuthentication.this;
			mWebPageTask.execute();

		} else {

			String json = Utils.getSharedPrefs(PinAuthentication.this, PrefUtils.KEY_USER);
			Gson gson = new Gson();
			if(json != "")
			user = gson.fromJson(json, User.class);

			if(user != null && user.getPin() != null){
				String _pin = user.getPin().trim();
				if(pin.equals(_pin)){
					gotoHomeScreen();

				}else{
				Utils.showToast(PinAuthentication.this, "Please enter a valid PIN");
				}
			}else{
				Utils.alertMessage(PinAuthentication.this, Utils.network_error);

			}
		}
	}
	
	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("output", ""+output);
		Time_emJsonParser parser = new Time_emJsonParser(PinAuthentication.this);
//		Boolean isAuthenticated = parser.authorizePIN(output);
		
		user = parser.getUserDetails(output , methodName);
		if(user!=null)
		{
			Utils.saveInSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER_ID,""+user.getId());
			PrefUtils.setIntegerPreference(getApplicationContext(), PrefUtils.KEY_ACTIVITY_ID,user.getActivityId());
			boolean value =user.isSignedIn();
			Log.e("boolean","boolean="+value);
			PrefUtils.setBooleanPreference(getApplicationContext(), PrefUtils.KEY_IS_SIGNED_IN,value);

			Gson gson = new Gson();
			String json = gson.toJson(user);
			Utils.saveInSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER, json );
			gotoHomeScreen();
		}
//		 HomeActivity.user.setSignedIn(Boolean.valueOf((Utils.getSharedPrefs(PinAuthentication.this, "isSignedIn").equals(""))?"false":"true"));
//		 HomeActivity.user.setActivityId(Integer.parseInt((Utils.getSharedPrefs(PinAuthentication.this, "activityId").equals(""))?"0":Utils.getSharedPrefs(PinAuthentication.this, "activityId")));
//		

		//else
			//Utils.showToast(PinAuthentication.this, "Please enter a valid PIN");

	}

	private void gotoHomeScreen()
	{
		Intent intent = new Intent(PinAuthentication.this, CompanyListActivity.class);
		intent.putExtra("trigger", "pin");
		startActivity(intent);
		finish();
	}
}

