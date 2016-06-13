package com.time_em.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.time_em.android.BaseActivity;
import com.time_em.android.DependencyResolver;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.authentication.ChangeStatusActivity;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.tasks.TaskListActivity;
import com.time_em.utils.GcmUtils;
import com.time_em.utils.Utils;

public class HomeActivity extends BaseActivity implements AsyncResponseTimeEm {

    private LinearLayout graphLayout;
    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    BarDataSet dataset;
    public DependencyResolver resolver;
    ArrayList<String> labels = new ArrayList<String>();
    public static User user;
    private LinearLayout changeStatus;
    private String trigger;
    private ImageView userStatus, imgStatus;
    private TextView txtUserStatus;
    private RecyclerView recyclerView;
    private Context context;
    ArrayList<TaskEntry> arrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        resolver = new DependencyResolver(context);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater
                .inflate(R.layout.activity_home, null, false);
        contentFrame.addView(contentView, 0);

//		addGraph();
        populatRecyclerView();
        registerDevice();
        initScreen();
        setClickListeners();

        if (trigger.equals("login"))
            openChangeStatusDialog();
    }

    private void addGraph() {
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

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
        chart.setLayoutParams(params);
        graphLayout.addView(chart);
    }

    private void populatRecyclerView() {

        arrayList = new ArrayList<>();
        Date myDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd");

        for (int i = 1; i <= 20; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, i);

            TaskEntry taskEntry = new TaskEntry();
            taskEntry.setTimeSpent(Double.valueOf(i));
            taskEntry.setCreatedDate(dateFormatter.format(calendar.getTime()));

            arrayList.add(taskEntry);
        }
//        for (int i = 0; i <= selectedPos; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(myDate);
//            calendar.add(Calendar.DAY_OF_YEAR, i);
//            arrayList.add(calendar);
//        }
    }

    private void initScreen() {
        recyclerView = (RecyclerView) findViewById(R.id.task_graph);
        recyclerView.setHasFixedSize(true);

        changeStatus = (LinearLayout) findViewById(R.id.changeStatus);
        userStatus = (ImageView) findViewById(R.id.userStatus);
        txtUserStatus = (TextView) findViewById(R.id.txtUserStatus);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        trigger = getIntent().getStringExtra("trigger");

        if (user.getUserTypeId() == 4)
            myTeam.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GraphAdapter  adapter = new GraphAdapter(arrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(TaskEntry item, int position) {

                Utils.showToast(HomeActivity.this, item.getCreatedDate() +" Clicked");

            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();
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
        if (HomeActivity.user.isSignedIn()) {
            resolver.pref().SetUserId(String.valueOf(HomeActivity.user.getId()));
            resolver.pref().SetActivityId(String.valueOf(HomeActivity.user.getActivityId()));

            menuUserStatus.setImageResource(R.drawable.user_online);
            userStatus.setImageResource(R.drawable.user_online);
            imgStatus.setImageResource(R.drawable.scan_signout);
            txtUserStatus.setText("Sign Out");
        } else {
            menuUserStatus.setImageResource(R.drawable.user_offline);
            userStatus.setImageResource(R.drawable.user_offline);
            imgStatus.setImageResource(R.drawable.scan_signin);
            txtUserStatus.setText("Sign In");
        }
    }

    private void registerDevice(){
        if (Utils.isNetworkAvailable(HomeActivity.this)) {

            String regId = GcmUtils.getRegistrationId(this);
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("UserID", ""+user.getId());
            postDataParameters.put("DeviceUId", regId);
            postDataParameters.put("DeviceOS", "android");

            Log.e(Utils.RegisterUserDevice,postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "post", Utils.RegisterUserDevice,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }

    public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {

        private final ArrayList<TaskEntry> items;
        private final OnItemClickListener listener;

        public GraphAdapter(ArrayList<TaskEntry> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_slider_row, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(items.get(position), listener, position);
        }

        @Override public int getItemCount() {
            return items.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView graphBar, date;

            public ViewHolder(View itemView) {
                super(itemView);
                graphBar = (TextView) itemView.findViewById(R.id.graphBar);
                date = (TextView) itemView.findViewById(R.id.date);
            }

            public void bind(final TaskEntry item, final OnItemClickListener listener, final int pos) {
                /*if(selectedPos==pos) {
                    date.setBackgroundResource(R.drawable.date_bg);
                    date.setTextColor(Color.WHITE);
                }else {
                    date.setBackgroundResource(R.drawable.date_bg_grey);
                    date.setTextColor(Color.BLACK);
                }*/
                Double val = (200/10) * item.getTimeSpent();
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,val.intValue());
                param.setMargins(10,10,10,0);
                param.addRule(RelativeLayout.ABOVE,date.getId());
                graphBar.setLayoutParams(param);
                date.setText(item.getCreatedDate());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        /*notifyItemChanged(pos);
                        selectedPos = pos;
                        notifyItemChanged(selectedPos);*/

                        listener.onItemClick(item, pos);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TaskEntry item, int position);
    }

    @Override
    public void processFinish(String output, String methodName) {
        Log.e(output,output);
    }
}
