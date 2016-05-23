package com.time_em.dashboard;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.time_em.android.BaseActivity;
import com.time_em.android.R;
import com.time_em.authentication.ChangeStatusActivity;
import com.time_em.model.User;
import com.time_em.tasks.TaskListActivity;

public class HomeActivity extends BaseActivity {

	private LinearLayout graphLayout;
	ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
	BarDataSet dataset;
	ArrayList<String> labels = new ArrayList<String>();
	public static User user;
	private LinearLayout changeStatus;
	private String trigger;
	private ImageView userStatus, imgStatus;
	private TextView txtUserStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = inflater
				.inflate(R.layout.activity_home, null, false);
		contentFrame.addView(contentView, 0);

//		addGraph();
		
		initScreen();
		setClickListeners();

		if(trigger.equals("login"))
			openChangeStatusDialog();
	}

	private void addGraph(){
		graphLayout = (LinearLayout) findViewById(R.id.graphLayout);

		entries.add(new BarEntry(4f, 0));
		entries.add(new BarEntry(8f, 1));
		entries.add(new BarEntry(6f, 2));
		entries.add(new BarEntry(12f, 3));
		entries.add(new BarEntry(18f, 4));
		entries.add(new BarEntry(9f, 5));
		entries.add(new BarEntry(4f, 6));
		entries.add(new BarEntry(8f, 7));
		entries.add(new BarEntry(6f, 8));
		entries.add(new BarEntry(12f, 9));
		entries.add(new BarEntry(18f, 10));
		entries.add(new BarEntry(9f, 11));
		entries.add(new BarEntry(4f, 12));
		entries.add(new BarEntry(8f, 13));
		entries.add(new BarEntry(6f, 14));
		entries.add(new BarEntry(12f, 15));
		entries.add(new BarEntry(18f, 16));
		entries.add(new BarEntry(9f, 17));
		entries.add(new BarEntry(4f, 18));
		entries.add(new BarEntry(8f, 19));
		entries.add(new BarEntry(6f, 20));
		entries.add(new BarEntry(12f, 21));
		entries.add(new BarEntry(18f, 22));
		entries.add(new BarEntry(9f, 23));

		dataset = new BarDataSet(entries, "# of Calls");

		labels.add("January");
		labels.add("February");
		labels.add("March");
		labels.add("April");
		labels.add("May");
		labels.add("June");
		labels.add("January1");
		labels.add("February1");
		labels.add("March1");
		labels.add("April1");
		labels.add("May1");
		labels.add("June1");
		labels.add("January2");
		labels.add("February2");
		labels.add("March2");
		labels.add("April2");
		labels.add("May2");
		labels.add("June2");
		labels.add("January3");
		labels.add("February3");
		labels.add("March3");
		labels.add("April3");
		labels.add("May3");
		labels.add("June3");

		BarChart chart = new BarChart(HomeActivity.this);

		BarData data = new BarData(labels, dataset);
		chart.setData(data);

		chart.setDescription("# of times Alice called Bob");

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		chart.setLayoutParams(params);
		graphLayout.addView(chart);
	}
	
	private void initScreen() {
		changeStatus = (LinearLayout) findViewById(R.id.changeStatus);
		userStatus = (ImageView)findViewById(R.id.userStatus);
		txtUserStatus = (TextView)findViewById(R.id.txtUserStatus);
		imgStatus = (ImageView)findViewById(R.id.imgStatus);
		trigger = getIntent().getStringExtra("trigger");
	}

	private void setClickListeners() {
		changeStatus.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == changeStatus) {
				openChangeStatusDialog();
			} 
		}
	};
	
	private void openChangeStatusDialog() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HomeActivity.this,
				ChangeStatusActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(HomeActivity.user.isSignedIn()) {
			menuUserStatus.setImageResource(R.drawable.user_online);
			userStatus.setImageResource(R.drawable.online);
			imgStatus.setImageResource(R.drawable.scan_signout);
			txtUserStatus.setText("Sign Out");
		}else {
			menuUserStatus.setImageResource(R.drawable.user_offline);
			userStatus.setImageResource(R.drawable.offline);
			imgStatus.setImageResource(R.drawable.scan_signin);
			txtUserStatus.setText("Sign In");
		}
	}
}
