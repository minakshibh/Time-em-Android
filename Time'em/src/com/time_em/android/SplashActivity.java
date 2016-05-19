package com.time_em.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.time_em.authentication.LoginActivity;
import com.time_em.authentication.PinAuthentication;
import com.time_em.utils.Utils;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				if(Utils.getSharedPrefs(SplashActivity.this, "loginId").equals("")){
					Intent i = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(i);
				}else{
					Intent i = new Intent(SplashActivity.this, PinAuthentication.class);
					startActivity(i);
				}

				// close this activity
				finish();
			}
		}, 2000);
    }
}
