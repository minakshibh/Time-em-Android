package com.time_em.asynctasks;

import java.util.HashMap;

import com.time_em.android.R;
import com.time_em.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

public class AsyncTaskTimeEm extends AsyncTask<String, Void, String> {

	//todo classes
	private Activity activity;
	private Context context;
	public AsyncResponseTimeEm delegate = null;
	private HashMap<String, String> postDataParameters;
	private JSONObject jsonObject = new JSONObject();

	//todo varibles
	private String result = "";
	private String name,pass;
	private boolean displayProgress,token;
	private String methodName, message,method_type,email;

	//todo widget
	private ProgressDialog pDialog;

	

	
	public AsyncTaskTimeEm(Activity activity, String method_type, String methodName, HashMap<String, String> postDataParameters, boolean displayDialog, String message) {
		this.activity = activity;
		this.method_type=method_type;
		this.methodName = methodName;
		this.postDataParameters = postDataParameters;
		this.displayProgress = displayDialog;
		this.message = message;
	}


	public AsyncTaskTimeEm(Activity activity, String method_type, String methodName, JSONObject jsonObject, boolean displayDialog, String message) {
		this.activity = activity;
		this.method_type=method_type;
		this.methodName = methodName;
		this.jsonObject = jsonObject;
		this.displayProgress = displayDialog;
		this.message = message;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		if(displayProgress){

			try {
				pDialog = new ProgressDialog(activity, R.style.MyTheme);
				//pDialog.setTitle("Time'em");
				//pDialog.setMessage(message);
				pDialog.setCancelable(false);
				pDialog.show();
			}catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	@Override
	protected String doInBackground(String... urls) {
		if(method_type.equalsIgnoreCase("get"))
		{
			result = Utils.getResponseFromUrlGet(token,methodName, postDataParameters, activity);
		
		}else if(method_type.equalsIgnoreCase("post") || method_type.equalsIgnoreCase("postJson")){
//			
			result = Utils.getResponseFromUrlPost(token,methodName, postDataParameters, activity, jsonObject, method_type);
		}
		
		return result;
	}

	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		int resultcode=0;
		try {
			if (displayProgress) {
				pDialog.dismiss();
			}
		}
			catch(Exception e)
			{e.printStackTrace();		}

		try{
		resultcode=Utils.resultCode();

		if(resultcode==200)
		{
			delegate.processFinish(result, methodName);
		}
		else if(result==null)
		{
			Toast.makeText(activity, Utils.Api_error, Toast.LENGTH_LONG).show();
		}
		else{
			//Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
			delegate.processFinish(result, methodName);
		}
			
		}
		catch (Exception e) {
			e.printStackTrace();

		}
	}
}
