package com.time_em.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.time_em.android.BaseActivity;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.TaskEntry;
import com.time_em.model.UserWorkSite;
import com.time_em.model.WorkSiteList;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;

public class TaskListActivity extends Activity implements AsyncResponseTimeEm{

    private ListView taskListview;
    private ArrayList<TaskEntry> tasks;
    private Time_emJsonParser parser;
    private int UserId;
    private ImageView addTaskButton, back;
    private TextView headerText,currentDate;
    private Intent intent;
    private LinearLayout footer;
    private RecyclerView recyclerView;
    private SimpleDateFormat apiDateFormater, dateFormatter, dayFormatter;
    ArrayList<Calendar> arrayList;
    private int selectedPos = 14;
    private String selectedDate;
    TimeEmDbHandler dbHandler;

    //for graphs
    private LinearLayout mainLinearLayout,lay_date,lay_hours;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        showTaskList();

       // currentDate.setText(Utils.formatDateChange(selectedDate,"MM-dd-yyyy","EEE dd MMM, yyyy"));
    }

    private void showTaskList()
    {
        populatRecyclerView();
        initScreen();
        setUpClickListeners();
        getTaskList(selectedDate);
    }
    private void showGraphs()
    {
        setContentView(R.layout.activity_graph);

        }
    private void initScreen() {
        dbHandler = new TimeEmDbHandler(TaskListActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        addTaskButton = (ImageView) findViewById(R.id.AddButton);
        back = (ImageView)findViewById(R.id.back);
        taskListview = (ListView) findViewById(R.id.taskList);
        parser = new Time_emJsonParser(TaskListActivity.this);
        headerText = (TextView)findViewById(R.id.headerText);
        try {
            UserId = HomeActivity.user.getId();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        currentDate=(TextView)findViewById(R.id.currentDate);
        currentDate.setVisibility(View.VISIBLE);


        if(UserId == HomeActivity.user.getId()){
            headerText.setText("My Tasks");
        }else{
            String username = getIntent().getStringExtra("UserName");
            headerText.setText(username+"'s Tasks");
        }
        footer = (LinearLayout)findViewById(R.id.footer);
        footer.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(TaskListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(selectedPos - 2, 20);
        recyclerView.setLayoutManager(layoutManager);
        apiDateFormater = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = apiDateFormater.format(arrayList.get(selectedPos).getTime());

        dateFormatter = new SimpleDateFormat("dd");
        dayFormatter = new SimpleDateFormat("E", Locale.US);



        DateSliderAdapter  adapter = new DateSliderAdapter(arrayList, new OnItemClickListener() {
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
                intent.putExtra("UserId", HomeActivity.user.getId());
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

        currentDate.setText(Utils.formatDateChange(selectedDate,"MM-dd-yyyy","EEE dd MMM, yyyy"));
       //if (Utils.isNetworkAvailable(TaskListActivity.this)) {

            String timeStamp = Utils.getSharedPrefs(TaskListActivity.this, UserId+"-"+selectedDate+"-" + getResources().getString(R.string.taskTimeStampStr));
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

            if(taskEntry.getId()==0) {
              //  BaseActivity.deleteIds.add(""+taskEntry.getId());
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                getTaskList(selectedDate);
            }
            else{
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("Id", String.valueOf(taskEntry.getId()));

            Log.e(Utils.deleteTaskAPI,""+postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    TaskListActivity.this, "post", Utils.deleteTaskAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
            mWebPageTask.execute();

            }

        } else {
            if(taskEntry.getId()==0) {
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                dbHandler.updateDeleteOffline(taskEntries, selectedDate);
                getTaskList(selectedDate);
            }
            else{
                taskEntry.setIsActive(false);
                ArrayList<TaskEntry> taskEntries = new ArrayList<>();
                taskEntries.add(taskEntry);
                HomeActivity.deleteIds.add("" + taskEntry.getId());
                dbHandler.updateTask(taskEntries, selectedDate,false);
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
        if(methodName.equals(Utils.getTaskListAPI)) {
            ArrayList<TaskEntry> taskEntries = parser.parseTaskList(output, UserId, selectedDate);

            dbHandler.updateTask(taskEntries, selectedDate,false);

            tasks = dbHandler.getTaskEnteries(UserId, selectedDate,false);
            taskListview.setAdapter(new TaskAdapter(TaskListActivity.this));
        }else if(methodName.equals(Utils.deleteTaskAPI)) {
            boolean error = parser.parseDeleteTaskResponse(output);
            if(!error) {
                getTaskList(selectedDate);
            }
        }
        else if(methodName.contains(Utils.GetUserWorksiteActivity))
        {
            ArrayList<UserWorkSite> array_worksite=  parser.getUserWorkSite(output);
            Log.e("UserWorkSite",""+array_worksite.size());
            settingGraph(array_worksite);
            }
    }

    public class DateSliderAdapter extends RecyclerView.Adapter<DateSliderAdapter.ViewHolder> {

        private final ArrayList<Calendar> items;
        private final OnItemClickListener listener;

        public DateSliderAdapter(ArrayList<Calendar> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_slider_row, parent, false);
            return new ViewHolder(v);
        }

        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(items.get(position), listener, position);
        }

        @Override public int getItemCount() {
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
                if(selectedPos==pos) {
                    date.setBackgroundResource(R.drawable.date_bg);
                    date.setTextColor(Color.WHITE);
                }else {
                    date.setBackgroundResource(R.drawable.date_bg_grey);
                    date.setTextColor(Color.BLACK);
                }

                date.setText(dateFormatter.format(item.getTime()));
                day.setText(dayFormatter.format(item.getTime()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
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

      //  if(getIntent().getStringExtra("UserName")!=null){

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

                GetUserWorkSiteApi();
                showGraphs();
            }
        //}
       // else {

           // showTaskList();
       // }

    }

    private void GetUserWorkSiteApi() {
        // http://timeemapi.azurewebsites.net/api/Worksite/GetUserWorksiteActivity
       // type: Get
       // parameter:userid

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("userid", String.valueOf(UserId));

        Log.e(""+Utils.GetUserWorksiteActivity, ""+postDataParameters.toString());

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                TaskListActivity.this, "get", Utils.GetUserWorksiteActivity,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) TaskListActivity.this;
        mWebPageTask.execute();
    }

    private void settingGraph(ArrayList<UserWorkSite> array_worksite) {
        mainLinearLayout = (LinearLayout)findViewById(R.id.graphLayout);
        lay_date= (LinearLayout)findViewById(R.id.lay_date);
        for(int i=0;i<array_worksite.size();i++) {

            // Create LinearLayout
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //linearLayout.setBackgroundColor(getResources().getColor(R.color.grey));
            linearLayout.setPadding(0, 10, 10, 0);

            // Add text view
            for (int j = 0; j <array_worksite.get(i).getArraylist_WorkSiteList().size(); j++) {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(30, 55));
                textView.setGravity(Gravity.CENTER);

                if (j == 0 | j == 5 | j == 8 | j == 10)// hex color 0xAARRGGBB
                    textView.setBackgroundColor(getResources().getColor(R.color.dullTextColor));
                if (j == 2 | j == 4 | j == 11 | j == 12 | j == 9)// hex color 0xAARRGGBB
                    textView.setBackgroundColor(getResources().getColor(R.color.cancelTextColor));
                if (j == 3 | j == 7 | j == 13 | j == 14 | j == 17 | j == 21 | j == 19)// hex color 0xAARRGGBB
                    textView.setBackgroundColor(getResources().getColor(R.color.black));
                if (j == 1 | j == 6 | j == 16 | j == 15 | j == 18 | j == 22 | j == 23 | j == 20)
                    textView.setBackgroundColor(0xff66ff66);


                textView.setPadding(0, 0, 5, 0);// in pixels (left, top, right, bottom)
                linearLayout.addView(textView);
            }

            mainLinearLayout.addView(linearLayout);

            //for date
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(60, 66));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setText("date" + i);
            textView.setPadding(0, 10, 0, 10);
            lay_date.addView(textView);
        }

    }
}