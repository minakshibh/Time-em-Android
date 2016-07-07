package com.time_em.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.ColorSiteId;
import com.time_em.model.TaskEntry;
import com.time_em.model.UserWorkSite;
import com.time_em.model.WorkSiteList;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

public class TaskListActivity extends Activity implements AsyncResponseTimeEm {

    private ListView taskListview;
    private ArrayList<TaskEntry> tasks;
    private Time_emJsonParser parser;
    private int UserId;
    private ImageView addTaskButton, back;
    private TextView headerText, currentDate;
    private Intent intent;
    private LinearLayout footer;
    private RecyclerView recyclerView;
    private SimpleDateFormat apiDateFormater, dateFormatter, dayFormatter;
    ArrayList<Calendar> arrayList;
    private int selectedPos = 14;
    private String selectedDate;
    TimeEmDbHandler dbHandler;
    TextView headerInfo;
    private Context context;
    int first_time = 1, totalWidth = 0;
    float oneMin, stratPoint, endPoint;
    ImageView addButton;
    LinearLayout lay_upperGraph, lay_colorIndicator;
    ArrayList<String> backGroundColor_array = new ArrayList<>();
    ArrayList<ColorSiteId> array_colorSiteId = new ArrayList<>();
    //for graphs
    private LinearLayout mainLinearLayout, lay_date, lay_hours;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // showTaskList();
        context = getApplicationContext();
        // currentDate.setText(Utils.formatDateChange(selectedDate,"MM-dd-yyyy","EEE dd MMM, yyyy"));
        //  GetUserWorkSiteApi();
    }

    private void showTaskList() {
        populatRecyclerView();
        initScreen();
        setUpClickListeners();
        getTaskList(selectedDate);
    }

    private void showGraphs() {
        setContentView(R.layout.activity_graph);
        setColorArray();
        lay_colorIndicator = (LinearLayout) findViewById(R.id.lay_colorIndicator);
        lay_upperGraph = (LinearLayout) findViewById(R.id.lay_upperGraph);
        lay_upperGraph.setVisibility(View.GONE);
        lay_hours = (LinearLayout) findViewById(R.id.lay_hours);
        lay_hours.setVisibility(View.GONE);
        headerInfo = (TextView) findViewById(R.id.headerText);
        headerInfo.setText("Graphs");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);
        addButton = (ImageView) findViewById(R.id.AddButton);
        addButton.setVisibility(View.GONE);


        //fetch from data base
        dbHandler = new TimeEmDbHandler(TaskListActivity.this);
        ArrayList<UserWorkSite> array_workSite=  dbHandler.getGeoGraphData();
        fetchDataGraphs(array_workSite);



        //getScreenWidth(TaskListActivity.this);//get width of screen

        //GetUserWorkSiteApi();
    }

    private void initScreen() {
        dbHandler = new TimeEmDbHandler(TaskListActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        addTaskButton = (ImageView) findViewById(R.id.AddButton);
        back = (ImageView) findViewById(R.id.back);
        taskListview = (ListView) findViewById(R.id.taskList);
        parser = new Time_emJsonParser(TaskListActivity.this);
        headerText = (TextView) findViewById(R.id.headerText);

        currentDate = (TextView) findViewById(R.id.currentDate);
        currentDate.setVisibility(View.VISIBLE);


          if(getIntent().getStringExtra("UserName")!=null) {
            String username = getIntent().getStringExtra("UserName");
            headerText.setText(username + "'s Tasks");
            addTaskButton.setVisibility(View.INVISIBLE);
            try{
                UserId =  Integer.parseInt(getIntent().getStringExtra("UserId"));
            }catch (Exception e){}
        }
        else{
            headerText.setText("My Tasks");
            try{
            UserId =   Integer.parseInt(Utils.getSharedPrefs(getApplicationContext(),"apiUserId"));
            }catch (Exception e){}
        }
        footer = (LinearLayout) findViewById(R.id.footer);
        footer.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TaskListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(selectedPos - 2, 20);
        recyclerView.setLayoutManager(layoutManager);
        apiDateFormater = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = apiDateFormater.format(arrayList.get(selectedPos).getTime());

        dateFormatter = new SimpleDateFormat("dd");
        dayFormatter = new SimpleDateFormat("E", Locale.US);


        DateSliderAdapter adapter = new DateSliderAdapter(arrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(Calendar item, int position) {
                String weekDay;
                // SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
                // weekDay = dayFormat.format(item.getTime());
                // Utils.showToast(TaskListActivity.this, item.get(Calendar.DAY_OF_MONTH)+" "+weekDay+" Clicked");

                selectedDate = apiDateFormater.format(item.getTime());
                getTaskList(selectedDate);

            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter
    }


    private void setUpClickListeners() {
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(TaskListActivity.this, AddEditTaskEntry.class);
                intent.putExtra("selectDate", selectedDate);
                intent.putExtra("UserId", ""+UserId);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        taskListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra("taskEntry", tasks.get(position));
                startActivity(intent);
            }
        });
    }

    private void populatRecyclerView() {

        arrayList = new ArrayList<>();
        Date myDate = new Date();

        for (int i = 0; i < selectedPos; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, i - selectedPos);
            arrayList.add(calendar);
        }
        for (int i = 0; i <= selectedPos; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, i);
            arrayList.add(calendar);
        }
    }

    private void getTaskList(String createdDate) {

        currentDate.setText(Utils.formatDateChange(selectedDate, "MM-dd-yyyy", "EEE dd MMM, yyyy"));
        //if (Utils.isNetworkAvailable(TaskListActivity.this)) {

        String timeStamp = Utils.getSharedPrefs(TaskListActivity.this, UserId + "-" + selectedDate + "-" + getResources().getString(R.string.taskTimeStampStr));
        if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
            timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("userId", String.valueOf(UserId));
        postDataParameters.put("createdDate", createdDate);
        postDataParameters.put("TimeStamp", timeStamp);

        Log.e("values", "userid: " + String.valueOf(UserId) + ", createdDate: " + createdDate + ", TimeStamp: " + timeStamp);

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                TaskListActivity.this, "post", Utils.getTaskListAPI,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
        mWebPageTask.execute();

        // } else {
        //    Utils.alertMessage(TaskListActivity.this, Utils.network_error);
        //    }
    }

    private void deleteTask(TaskEntry taskEntry) {

        if (Utils.isNetworkAvailable(TaskListActivity.this)) {

            if (taskEntry.getId() == 0) {
                //  BaseActivity.deleteIds.add(""+taskEntry.getId());
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                getTaskList(selectedDate);
            } else {
                HashMap<String, String> postDataParameters = new HashMap<String, String>();
                postDataParameters.put("Id", String.valueOf(taskEntry.getId()));

                Log.e(Utils.deleteTaskAPI, "" + postDataParameters.toString());
                AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                        TaskListActivity.this, "post", Utils.deleteTaskAPI,
                        postDataParameters, true, "Please wait...");
                mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
                mWebPageTask.execute();

            }

        } else {
            if (taskEntry.getId() == 0) {
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                getTaskList(selectedDate);
            } else {
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                HomeActivity.deleteIds.add("" + taskEntry.getId());
                dbHandler.updateTask(taskEntries, selectedDate, false);
            }
            // Utils.alertMessage(TaskListActivity.this, "Task Updated Successfully.!");
            //Utils.alertMessage(TaskListActivity.this, Utils.network_error);
        }
    }



    public class TaskAdapter extends BaseSwipeAdapter {
        private Context context;
        private TextView taskName, hours, taskComments;

        private LinearLayout edit, delete;

        public TaskAdapter(Context ctx) {
            context = ctx;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tasks.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return tasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public void fillValues(final int position, View convertView) {
            // TODO Auto-generated method stub

            final TaskEntry selectedTask = tasks.get(position);

            taskName = (TextView) convertView.findViewById(R.id.taskName);
            hours = (TextView) convertView.findViewById(R.id.hours);
            delete = (LinearLayout) convertView.findViewById(R.id.delete);
            edit = (LinearLayout) convertView.findViewById(R.id.edit);
            taskComments = (TextView) convertView.findViewById(R.id.taskComments);

            taskName.setText(selectedTask.getTaskName());
            taskComments.setText(selectedTask.getComments());
            hours.setText("(" + String.valueOf(selectedTask.getTimeSpent()) + ") Hours");

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            TaskListActivity.this);
                    alert.setTitle("Delete this task?");
                    alert.setMessage("Are you sure?");
                    alert.setPositiveButton("No", null);
                    alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask(tasks.get(position));
                        }
                    });

                    alert.show();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(TaskListActivity.this, AddEditTaskEntry.class);
                    intent.putExtra("selectDate", selectedDate);
                    intent.putExtra("taskEntry", selectedTask);
                    startActivity(intent);
                }
            });
        }

        @Override
        public View generateView(int arg0, ViewGroup arg1) {
            return LayoutInflater.from(TaskListActivity.this).inflate(
                    R.layout.task_row, null);

        }

        @Override
        public int getSwipeLayoutResourceId(int arg0) {
            // TODO Auto-generated method stub
            return R.id.swipe;
        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        // TODO Auto-generated method stub
        Log.e("output", ":: " + output);
        if (methodName.equals(Utils.getTaskListAPI)) {
            ArrayList<TaskEntry> taskEntries = parser.parseTaskList(output, UserId, selectedDate);

            dbHandler.updateTask(taskEntries, selectedDate, false);

            tasks = dbHandler.getTaskEnteries(UserId, selectedDate, false);
            taskListview.setAdapter(new TaskAdapter(TaskListActivity.this));
        } else if (methodName.equals(Utils.deleteTaskAPI)) {
            boolean error = parser.parseDeleteTaskResponse(output);
            if (!error) {
                getTaskList(selectedDate);
            }
        }

       /* else if (methodName.contains(Utils.GetUserWorksiteActivity)) {
            parser = new Time_emJsonParser(TaskListActivity.this);
            ArrayList<UserWorkSite> array_worksite = parser.getUserWorkSite(output);
            dbHandler.deleteGeoGraphs();
            for(int i=0;i<array_worksite.size();i++) {
                Gson gson = new Gson();
                // This can be any object. Does not have to be an arraylist.
                String allData = gson.toJson(array_worksite.get(i).getArraylist_WorkSiteList());
                dbHandler.updateGeoGraphData(allData, array_worksite.get(i).getDate());
            }
            array_worksite=  dbHandler.getGeoGraphData();
            fetchDataGraphs(array_worksite);
           // array_worksite.clear();
          //  array_worksite= dbHandler.getGeoGraphData("10");
            //Log.e("UserWorkSite", "" + array_worksite.size());
            //setColorWithModel(array_worksite);
            //settingGraph(array_worksite); // setting graphs with bar



        }*/
    }
    public static <WorkSiteList> List<com.time_em.model.WorkSiteList> stringToArray(String s, Class<com.time_em.model.WorkSiteList[]> clazz) {
        com.time_em.model.WorkSiteList[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }
    private void fetchDataGraphs(ArrayList<UserWorkSite> arrayList) {
        //Gson gson = new Gson();
        ArrayList<UserWorkSite> array_UserWorkSite= new ArrayList<UserWorkSite>();
        for(int i=0;i<arrayList.size();i++) {
           String allData= arrayList.get(i).getAllData();
           String date= arrayList.get(i).getDate();
           List<WorkSiteList> arrayList_WorkSiteList= stringToArray(allData, WorkSiteList[].class);


            Log.e("size=",""+arrayList_WorkSiteList.size());
            UserWorkSite userWorkSite=new UserWorkSite();
            userWorkSite.setDate(date);
            ArrayList<WorkSiteList> array_WorkSiteList= new ArrayList<WorkSiteList>();

            for(int j=0;j<arrayList_WorkSiteList.size();j++) {
                WorkSiteList workSiteList=new WorkSiteList();
                workSiteList.setWorkSiteId(stringToArray(allData, WorkSiteList[].class).get(j).getWorkSiteId());
                workSiteList.setWorkSiteName(stringToArray(allData, WorkSiteList[].class).get(j).getWorkSiteName());
                workSiteList.setWorkingHour(stringToArray(allData, WorkSiteList[].class).get(j).getWorkingHour());
                workSiteList.setTimeIn(stringToArray(allData, WorkSiteList[].class).get(j).getTimeIn());
                workSiteList.setTimeOut(stringToArray(allData, WorkSiteList[].class).get(j).getTimeOut());
               array_WorkSiteList.add(workSiteList);
            }

            userWorkSite.setArraylist_WorkSiteList(array_WorkSiteList);
            array_UserWorkSite.add(userWorkSite);
        }
        array_UserWorkSite.size();
        Log.e("total","ss="+array_UserWorkSite.size());
        setColorWithModel(array_UserWorkSite);
        settingGraph(array_UserWorkSite); //
    }

    private void setColorWithModel(ArrayList<UserWorkSite> array_worksite) {
        if (array_worksite != null && array_worksite.size() > 0) {
            array_colorSiteId = new ArrayList<>();
            for (int i = 0; i < array_worksite.size(); i++) {

                for (int j = 0; j < array_worksite.get(i).getArraylist_WorkSiteList().size(); j++) {
                    String siteId = array_worksite.get(i).getArraylist_WorkSiteList().get(j).getWorkSiteName();

                    ColorSiteId colorSiteId = new ColorSiteId();
                    if(j>10) {
                        String position=i+""+j;
                        int pos=Integer.parseInt(position);
                        colorSiteId.setColor(backGroundColor_array.get(pos));
                        Log.e("pos", "" + pos);
                    }else{
                        colorSiteId.setColor(backGroundColor_array.get(i + j));
                        Log.e("pos", "" + i + j+backGroundColor_array.get(i + j));
                    }
                    colorSiteId.setSietId(siteId);
                    boolean value=true;
                    for(int k=0;k<array_colorSiteId.size();k++) {
                       if(siteId.equalsIgnoreCase(array_colorSiteId.get(k).getSietId())) {
                           value=false;
                       }

                    }
                    if(value){
                        array_colorSiteId.add(colorSiteId);
                    }
                }
                Log.e("color size", "" + array_colorSiteId.size());
            }
           /* array_colorSiteId = new ArrayList<>();
            ColorSiteId colorSiteId = new ColorSiteId();
            colorSiteId.setSietId("OSBORNE PARK");
            colorSiteId.setColor(backGroundColor_array.get(1));
            array_colorSiteId.add(colorSiteId);

            colorSiteId = new ColorSiteId();
            colorSiteId.setSietId("LCPL - GCSB");
            colorSiteId.setColor(backGroundColor_array.get(2));
            array_colorSiteId.add(colorSiteId);*/


          //  ArrayList<ColorSiteId> array_ColorSiteIdDup= removeDuplicateValue(array_colorSiteId);
           // setColor(removeDuplicateValue(array_ColorSiteIdDup));// set color to indicator
            setColor(array_colorSiteId);
        }
    }

    /*private ArrayList<ColorSiteId> removeDuplicateValue(ArrayList<ColorSiteId> array_colorIds) {

        ArrayList<ColorSiteId> array_colorSiteId = new ArrayList<>();


      *//*  for(ColorSiteId user1 : array_colorIds) {
            for (ColorSiteId user2 : array_colorIds) {
                if (!user1.getSietId().equals(user2.getSietId())) {
                    array_colorSiteId.add(user1);
                }
            }
        }*//*
      return array_colorSiteId;

    }*/

    public class DateSliderAdapter extends RecyclerView.Adapter<DateSliderAdapter.ViewHolder> {

        private final ArrayList<Calendar> items;
        private final OnItemClickListener listener;

        public DateSliderAdapter(ArrayList<Calendar> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_slider_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(items.get(position), listener, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView day, date;

            public ViewHolder(View itemView) {
                super(itemView);
                day = (TextView) itemView.findViewById(R.id.day);
                date = (TextView) itemView.findViewById(R.id.date);
            }

            public void bind(final Calendar item, final OnItemClickListener listener, final int pos) {
                if (selectedPos == pos) {
                    date.setBackgroundResource(R.drawable.date_bg);
                    date.setTextColor(Color.WHITE);
                } else {
                    date.setBackgroundResource(R.drawable.date_bg_grey);
                    date.setTextColor(Color.BLACK);
                }

                date.setText(dateFormatter.format(item.getTime()));
                day.setText(dayFormatter.format(item.getTime()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(selectedPos);
                        selectedPos = pos;
                        notifyItemChanged(selectedPos);

                        listener.onItemClick(item, pos);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Calendar item, int position);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getStringExtra("UserName") != null) {

            int value = TaskListActivity.this.getResources().getConfiguration().orientation;
            String orientation = "";
            if (value == Configuration.ORIENTATION_PORTRAIT) {
                orientation = "Portrait";
                //  Toast.makeText(this, "PORTRAIT", Toast.LENGTH_SHORT).show();
                showTaskList();
            }

            if (value == Configuration.ORIENTATION_LANDSCAPE) {
                orientation = "Landscape";
                //   Toast.makeText(this, "LANDSCAPE", Toast.LENGTH_SHORT).show();
                showGraphs();


            }
        } else {

            showTaskList();
        }

    }

    /*private void GetUserWorkSiteApi() {

        UserId = HomeActivity.user.getId();
        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("userid", String.valueOf(UserId));

        Log.e("" + Utils.GetUserWorksiteActivity, "" + postDataParameters.toString());

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                TaskListActivity.this, "get", Utils.GetUserWorksiteActivity,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
        mWebPageTask.execute();
    }*/

    private void settingGraph(ArrayList<UserWorkSite> array_worksite) {
        lay_upperGraph = (LinearLayout) findViewById(R.id.lay_upperGraph);
        lay_upperGraph.setVisibility(View.VISIBLE);
        lay_hours = (LinearLayout) findViewById(R.id.lay_hours);
        lay_hours.setVisibility(View.VISIBLE);
        mainLinearLayout = (LinearLayout) findViewById(R.id.graphLayout);
        lay_hours = (LinearLayout) findViewById(R.id.lay_hours);
        lay_date = (LinearLayout) findViewById(R.id.lay_date);
        totalWidth = getScreenWidth(TaskListActivity.this);
        mainLinearLayout.removeAllViews();
        lay_date.removeAllViews();
        for (int i = 0; i < array_worksite.size(); i++) {

            // Create LinearLayout
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(0, 10, 10, 0);

            //   float totalWidth= lay_hours.getWidth();
            float oneHour = totalWidth / 24;
            float totalMins = 24 * 60;
            oneMin = totalWidth / totalMins;

            // Add text view
            if (array_worksite.get(i).getArraylist_WorkSiteList() != null && array_worksite.get(i).getArraylist_WorkSiteList().size() > 0) {
                for (int j = 0; j < array_worksite.get(i).getArraylist_WorkSiteList().size(); j++) {
                    String value=null,valueIn=null,valueOut=null;
                    value = array_worksite.get(i).getArraylist_WorkSiteList().get(j).getWorkingHour();

                     valueIn = array_worksite.get(i).getArraylist_WorkSiteList().get(j).getTimeIn();
                     valueOut = array_worksite.get(i).getArraylist_WorkSiteList().get(j).getTimeOut();
                    Log.e("hours", "hours=" + value+" valueIn=" + valueIn+" valueOut=" + valueOut);


                    if (first_time == 1) {
                        first_time = 0;
                        endPoint = getStartTime(valueIn);
                        float width = getDifferenceTwoMins(0, endPoint);
                        Log.e("width", "" + width);
                        float oneWidth = width * oneMin;
                        int int_width = (int) oneWidth;
                        View view = new TextView(this);
                        view.setPadding(0, 0, 0, 0);// in pixels (left, top, right, bottom)
                        view.setLayoutParams(new LinearLayout.LayoutParams(int_width, 55));
                        view.setBackgroundColor(getResources().getColor(R.color.white));
                        linearLayout.addView(view);
                    }
                    if (endPoint != getStartTime(valueIn) &&  endPoint!=0) {
                        // endPoint = getStartTime(valueIn);
                        if(value!=null
                                        && !value.equalsIgnoreCase("0.0")
                                        && valueIn!=null
                                        && !valueIn.equalsIgnoreCase("null")
                                        && valueOut!=null
                                        && !valueOut.equalsIgnoreCase("null")


                                ) {

                            float width = getDifferenceTwoMins(endPoint, getStartTime(valueIn));
                            Log.e("width", "" + width);
                            float oneWidth = width * oneMin;
                            int int_width = (int) oneWidth;
                            View view = new TextView(this);
                            view.setPadding(0, 0, 0, 0);// in pixels (left, top, right, bottom)
                            view.setLayoutParams(new LinearLayout.LayoutParams(int_width, 55));
                            view.setBackgroundColor(getResources().getColor(R.color.white));
                            linearLayout.addView(view);
                        }
                    }
                    stratPoint = getStartTime(valueIn);
                    endPoint = getStartTime(valueOut);
                    float width = getDifferenceTwoMins(stratPoint, endPoint);

                    Log.e("difference width", "" + width);
                    float one_width = width * oneMin;
                    //float widtha= width+Float.parseFloat(value);
                    int int_width = (int) one_width;
                    Log.e("total_width", "" + int_width);
                    if(int_width>0) {
                        View view = new TextView(this);
                        view.setPadding(0, 0, 0, 0);// in pixels (left, top, right, bottom)
                        view.setLayoutParams(new LinearLayout.LayoutParams(int_width, 55));
                        // view.setBackgroundColor(getResources().getColor(R.color.black));

                       String id = array_worksite.get(i).getArraylist_WorkSiteList().get(j).getWorkSiteName();
                        if (array_colorSiteId != null && array_colorSiteId.size() > 0) {
                            for (int k = 0; k < array_colorSiteId.size(); k++) {
                                if (id.equalsIgnoreCase(array_colorSiteId.get(k).getSietId())) {
                                    view.setBackgroundColor(Color.parseColor(array_colorSiteId.get(k).getColor()));
                                }
                            }
                        }
                        linearLayout.addView(view);
                        //  view.setBackgroundColor(getResources().getColor(R.color.black));


                     /*   if (j == 0) {
                            view.setBackgroundColor(getResources().getColor(R.color.cancelTextColor));

                        } else if (j == 1) {
                            String Id = array_worksite.get(i).getArraylist_WorkSiteList().get(0).getWorkSiteId();
                            String Id2 = array_worksite.get(i).getArraylist_WorkSiteList().get(1).getWorkSiteId();
                            if (Id.equalsIgnoreCase(Id2)) {
                                view.setBackgroundColor(getResources().getColor(R.color.cancelTextColor));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.sendTextColor));
                            }
                        } else if (j == 2) {
                            String Id = array_worksite.get(i).getArraylist_WorkSiteList().get(0).getWorkSiteId();
                            String Id2 = array_worksite.get(i).getArraylist_WorkSiteList().get(1).getWorkSiteId();
                            String Id3 = array_worksite.get(i).getArraylist_WorkSiteList().get(2).getWorkSiteId();
                            if (Id.equalsIgnoreCase(Id2)) {
                                view.setBackgroundColor(getResources().getColor(R.color.cancelTextColor));
                            } else if (Id.equalsIgnoreCase(Id3)) {
                                view.setBackgroundColor(getResources().getColor(R.color.sendTextColor));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.alphabeticalTextColor));
                            }
                        }*/


                    }
                }
            } else {
                TextView textView = new TextView(this);
                textView.setPadding((int) 0, 0, 0, 0);// in pixels (left, top, right, bottom)
                textView.setLayoutParams(new LinearLayout.LayoutParams(0, 56));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setBackgroundColor(getResources().getColor(R.color.white));
                linearLayout.addView(textView);
            }
            first_time = 1;
            mainLinearLayout.addView(linearLayout);

            //for date
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(60, 66));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setText(array_worksite.get(i).getDate().substring(0, 5));
            textView.setTextSize(12);
            textView.setPadding(0, 10, 0, 10);
            lay_date.addView(textView);
        }

    }

    private float getStartTime(String startTime) {
        try {
            String[] parts = startTime.split(":");
            String part1 = parts[0]; //
            String part2 = parts[1]; //
            float fPart1 = Float.parseFloat(part1);
            float fPart2 = Float.parseFloat(part2);
            float hoursInMins = fPart1 * 60;
            float totalMins = hoursInMins + fPart2;
            return totalMins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    private float getDifferenceTwoMins(float startTime, float endTime) {
        try {
            float totalWidth = endTime - startTime;
            return totalWidth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

   /* private float getDifferenceTwoPointsHours(String startTime, String endTime) {
        try {
            float start = Float.parseFloat(startTime.replace(":", "."));
            float end = Float.parseFloat(endTime.replace(":", "."));

            Float totalTime = end - start;
            Log.d("totalTime", "" + totalTime);
            float totalWidth = totalTime;
            return totalWidth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }*/

    private void setColor(ArrayList<ColorSiteId> arrayList) {
           lay_colorIndicator.removeAllViews();
         for(int i=0;i<arrayList.size();i++) {
             LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             View rowView = inflater.inflate(R.layout.color_indicator, null);
             TextView txt_color = (TextView) rowView.findViewById(R.id.txt_color);
             TextView txt_name = (TextView) rowView.findViewById(R.id.txt_name);
             txt_name.setText(arrayList.get(i).getSietId());
             txt_color.setBackgroundColor(Color.parseColor(arrayList.get(i).getColor()));
             lay_colorIndicator.addView(rowView);
         }
    }

    private int getScreenWidth(Activity activity)
    {
        WindowManager w = activity.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        int  widthPixels = metrics.widthPixels;
        long  heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d)-200;
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
                Log.e("widthPixels","widthPixels="+widthPixels+" heightPixels="+heightPixels);
                return widthPixels;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x-200;
                heightPixels = realSize.y;

                Log.e("widthPixels","widthPixels="+widthPixels+" heightPixels="+heightPixels);
                return widthPixels;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        return 650;
    }
    private void setColorArray() {

        backGroundColor_array.clear();
        backGroundColor_array = new ArrayList<>();
        backGroundColor_array.add("#63C070");
        backGroundColor_array.add("#B83F3A");
        backGroundColor_array.add("#51B3CE");
        backGroundColor_array.add("#63C070");
        backGroundColor_array.add("#FFFAF0");
        backGroundColor_array.add("#FF55F0");
        backGroundColor_array.add("#FFFF00");
        backGroundColor_array.add("#B0171F");
        backGroundColor_array.add("#FFB6C1");
        backGroundColor_array.add("#EEA2AD");
        backGroundColor_array.add("#8B5F65");
        backGroundColor_array.add("#DA70D6");
        backGroundColor_array.add("#FF00FF");
        backGroundColor_array.add("#912CEE");
        backGroundColor_array.add("#551A8B");
        backGroundColor_array.add("#9F79EE");
        backGroundColor_array.add("#0000FF");
        backGroundColor_array.add("#00008B");
        backGroundColor_array.add("#3D59AB");
        backGroundColor_array.add("#27408B");
        backGroundColor_array.add("#B0C4DE");
        backGroundColor_array.add("#6E7B8B");
        backGroundColor_array.add("#00B2EE");
        backGroundColor_array.add("#00FA9A");
        backGroundColor_array.add("#98FB98");
        backGroundColor_array.add("#FFFF00");
        backGroundColor_array.add("#BDB76B");
        backGroundColor_array.add("#FCE6C9");
        backGroundColor_array.add("#CDAA7D");
        backGroundColor_array.add("#00008b");
        backGroundColor_array.add("#cd3333");
        backGroundColor_array.add("#5f9ea0");
        backGroundColor_array.add("#8ee5ee");
        backGroundColor_array.add("#7fff00");


        backGroundColor_array.add("#63C070");
        backGroundColor_array.add("#51B3CE");
        backGroundColor_array.add("#63C070");
        backGroundColor_array.add("#B83F3A");
        backGroundColor_array.add("#FFFAF0");
        backGroundColor_array.add("#FFFFF0");
        backGroundColor_array.add("#FFFAFA");
        backGroundColor_array.add("#FFFF00");
        backGroundColor_array.add("#B0171F");
        backGroundColor_array.add("#FFB6C1");
        backGroundColor_array.add("#EEA2AD");
        backGroundColor_array.add("#8B5F65");
        backGroundColor_array.add("#DA70D6");
        backGroundColor_array.add("#FF00FF");
        backGroundColor_array.add("#912CEE");
        backGroundColor_array.add("#551A8B");
        backGroundColor_array.add("#9F79EE");
        backGroundColor_array.add("#0000FF");
        backGroundColor_array.add("#00008B");
        backGroundColor_array.add("#3D59AB");
        backGroundColor_array.add("#27408B");
        backGroundColor_array.add("#B0C4DE");
        backGroundColor_array.add("#6E7B8B");
        backGroundColor_array.add("#00B2EE");
        backGroundColor_array.add("#00FA9A");
        backGroundColor_array.add("#98FB98");
        backGroundColor_array.add("#FFFF00");
        backGroundColor_array.add("#BDB76B");
        backGroundColor_array.add("#FCE6C9");
        backGroundColor_array.add("#CDAA7D");
        backGroundColor_array.add("#00008b");
        backGroundColor_array.add("#cd3333");
        backGroundColor_array.add("#5f9ea0");
        backGroundColor_array.add("#8ee5ee");
        backGroundColor_array.add("#7fff00");

    }
}