package com.time_em.asynctasks;

import java.util.HashMap;

import com.time_em.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;


public class AsyncTaskTimeEm extends AsyncTask<String, Void, String> {

	private Activity activity;
	public AsyncResponseTimeEm delegate = null;
	private String result = "";	
	private  ProgressDialog pDialog;
	private String methodName, message,method_type,email;
	private HashMap<String, String> postDataParameters;
	private boolean displayProgress,token;
	
	
	private String name,pass;
	
	public AsyncTaskTimeEm(Activity activity, String method_type, String methodName, HashMap<String, String> postDataParameters, boolean displayDialog, String message) {
		this.activity = activity;
		this.method_type=method_type;
		this.methodName = methodName;
		this.postDataParameters = postDataParameters;
		this.displayProgress = displayDialog;
		this.message = message;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		if(displayProgress){
			pDialog = new ProgressDialog(activity);
			pDialog.setTitle("Time'em");
			pDialog.setMessage(message);
			pDialog.setCancelable(false);
			pDialog.show();
		}
	}

	@Override
	protected String doInBackground(String... urls) {
		if(method_type.equalsIgnoreCase("get"))
		{
			result = Utils.getResponseFromUrlGet(token,methodName, postDataParameters, activity);
		
		}else if(method_type.equalsIgnoreCase("post")){
//			
			result = Utils.getResponseFromUrlPost(token,methodName, postDataParameters, activity);
		}
		
		return result;
	}

	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		int resultcode=0;
		try{
		if(displayProgress)
				
		pDialog.dismiss();
		resultcode=Utils.resultCode();

		if(resultcode==200)
		{
			delegate.processFinish(result, methodName);
		}else if(result==null)
		{
			
			Toast.makeText(activity, "Something went wrong. Please try again..", Toast.LENGTH_LONG).show();	
		}else{
			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
			delegate.processFinish(result, methodName);
		}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
