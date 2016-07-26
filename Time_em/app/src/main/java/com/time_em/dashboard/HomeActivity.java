package com.time_em.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.time_em.android.BaseActivity;
import com.time_em.android.DependencyResolver;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.authentication.ChangeStatusActivity;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.geofencing.BackgroundLocationService;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.Notification;
import com.time_em.model.SpinnerData;
import com.time_em.model.SyncData;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.model.UserWorkSite;
import com.time_em.model.Widget;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.FileUtils;
import com.time_em.utils.GcmUtils;
import com.time_em.utils.SpinnerTypeAdapter;
import com.time_em.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends BaseActivity implements AsyncResponseTimeEm, TabLayout.OnTabSelectedListener {

    private LinearLayout graphLayout, lay_indicator;
    BarDataSet dataset;
    public DependencyResolver resolver;
    public static User user;
    private LinearLayout changeStatus;
    private String trigger;
    private ImageView userStatus, imgStatus;
    private TextView txtUserStatus;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private TextView currentDate;
    private LinearLayout AddWigdetView;
    private TextView AddNewWidgetTextVew;


    private Time_emJsonParser parser;
    private Double maxValueTask = 0.0, maxValueSignInOut = 0.0;
    private TabLayout tabLayout;
    private int graphBarHeight = 140;
    private TimeEmDbHandler dbHandler;
    private FileUtils fileUtils;
    private Intent intent;
    private Context context;

    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<TaskEntry> arrayList = new ArrayList<>();
    ArrayList<TaskEntry> arrayList_SignInOut = new ArrayList<>();
    private ArrayList<Notification> notifications = new ArrayList<>();
    private ArrayList<TaskEntry> tasks = new ArrayList<>();
    private ArrayList<Notification> notifications_delete = new ArrayList<>();
    private ArrayList<TaskEntry> tasks_delete = new ArrayList<>();
    public static ArrayList<String> deleteIds = new ArrayList<>();
    private  ArrayList<Widget> Home_arrayList_widget = new ArrayList<Widget>();


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

        fetchTaskGraphsData();
        fetchGraphsSignInOut();
        registerDevice();
        initScreen();
        setClickListeners();
        setTapBar();
        startLocationService(getApplicationContext());

        // apis
        if(resolver.pref().getApiCheck()) {
            loadProjects();
            getTaskList();
            loadNotificationTypes();
            loadRecipients();
            getUserList(user.getId());
            getNotificationList();
            resolver.pref().setApiCheck(false);
        }

        if (trigger.equals("login"))
            openChangeStatusDialog();

    }


    private void initScreen() {
        fileUtils=new FileUtils(HomeActivity.this);
        dbHandler = new TimeEmDbHandler(HomeActivity.this);
        sync = (RelativeLayout) findViewById(R.id.sync);
        viewPager = (ViewPager) findViewById(R.id.pager);
        changeStatus = (LinearLayout) findViewById(R.id.changeStatus);
        AddWigdetView = (LinearLayout) findViewById(R.id.AddWidgetView);
        AddNewWidgetTextVew = (TextView) findViewById(R.id.AddNewWidgetTextVew);
        userStatus = (ImageView) findViewById(R.id.userStatus);
        txtUserStatus = (TextView) findViewById(R.id.txtUserStatus);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        trigger = getIntent().getStringExtra("trigger");
        lay_indicator = (LinearLayout) findViewById(R.id.lay_indicator);
        currentDate = (TextView) findViewById(R.id.currentDate);
        currentDate.setText(Utils.getCurrentDate());
        parser = new Time_emJsonParser(HomeActivity.this);
        try {
            if (user.getUserTypeId() == 4)
                myTeam.setVisibility(View.GONE);
        }catch (Exception e) {
            e.printStackTrace();
        }
        SetUpWigdet();

    }

    private void SetUpWigdet() {

        LayoutInflater inflater = getLayoutInflater();
        View rowView = inflater.inflate(R.layout.template_add_widget_view, null);
    }

    private void setTapBar() {
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("UserGraph"));
        tabLayout.addTab(tabLayout.newTab().setText("UserLoginGraph"));
        // tabLayout.addTab(tabLayout.newTab().setText("Tab3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

    }

    private void setClickListeners() {
        changeStatus.setOnClickListener(listener);
        sync.setOnClickListener(listener);
        AddNewWidgetTextVew.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == changeStatus) {
                openChangeStatusDialog();
            } else if (v == sync) {
                if (mDrawerLayout.isDrawerOpen(flyoutDrawerRl)) {
                    mDrawerLayout.closeDrawers();
                    syncUploadData();
                }
            }
            else if(v==AddNewWidgetTextVew) {
                intent = new Intent(HomeActivity.this, AddWigdetActvity.class);
                startActivity(intent);
            }
        }
    };

    private void openChangeStatusDialog() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(HomeActivity.this, ChangeStatusActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncDataCheck();
        addWidget();
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

    private void registerDevice() {
        if (Utils.isNetworkAvailable(HomeActivity.this)) {

            String regId = GcmUtils.getRegistrationId(this);
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("UserID", "" + user.getId());
            postDataParameters.put("DeviceUId", regId);
            postDataParameters.put("DeviceOS", "android");

            Log.e(Utils.RegisterUserDevice, postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "post", Utils.RegisterUserDevice,
                    postDataParameters, false, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }

    private void fetchTaskGraphsData() {
        if (Utils.isNetworkAvailable(HomeActivity.this)) {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("userid", "" + user.getId());

            //http://timeemapi.azurewebsites.net/api/usertask/UserTaskGraph?userid=2
            Log.e(Utils.UserTaskGraph, postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "get", Utils.UserTaskGraph,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }

    private void fetchGraphsSignInOut() {
        if (Utils.isNetworkAvailable(HomeActivity.this)) {

            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("userid", "" + user.getId());

            // http://timeemapi.azurewebsites.net/api/usertask/UsersGraph?userid=2
            Log.e(Utils.UsersGraph, postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "get", Utils.UsersGraph,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }

    private void firstGraphView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GraphAdapter adapter = new GraphAdapter(true, arrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(TaskEntry item, int position) {

               // Utils.showToast(HomeActivity.this, item.getCreatedDate() + " Clicked");

            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();
    }

    private void secondGraphView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GraphAdapter adapter = new GraphAdapter(false, arrayList_SignInOut, new OnItemClickListener() {
            @Override
            public void onItemClick(TaskEntry item, int position) {
                //Utils.showToast(HomeActivity.this, item.getCreatedDate() + " Clicked");
            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();
    }

    private void loadProjects() {
        //   if (Utils.isNetworkAvailable(AddEditTaskEntry.this)) {
        int getSPrefsId = Integer.parseInt(Utils.getSharedPrefs(getApplicationContext(),"apiUserId"));
        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("userId", String.valueOf(getSPrefsId));

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "get", Utils.GetAssignedTaskList,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

        // } else {
        //      Utils.alertMessage(AddEditTaskEntry.this, Utils.network_error);
        //  }
    }

    private void getTaskList() {
        //currentDate.setText(Utils.formatDateChange(selectedDate, "MM-dd-yyyy", "EEE dd MMM, yyyy"));
        //if (Utils.isNetworkAvailable(TaskListActivity.this)) {

        //String timeStamp = Utils.getSharedPrefs(HomeActivity.this, UserId + "-" + selectedDate + "-" + getResources().getString(R.string.taskTimeStampStr));
       // if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
        //    timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        //postDataParameters.put("userId", String.valueOf(UserId));
        postDataParameters.put("userId",String.valueOf(user.getId()));
        postDataParameters.put("createdDate","");
        postDataParameters.put("TimeStamp", "");

       // Log.e("values"+Utils.getTaskListAPI, "userid: " + String.valueOf(UserId) + ", createdDate: " + createdDate + ", TimeStamp: " + timeStamp);
       // Log.e("" + Utils.getTaskListAPI, "" + postDataParameters.toString());

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "post", Utils.getTaskListAPI,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

        // } else {
        //    Utils.alertMessage(TaskListActivity.this, Utils.network_error);
        //    }
    }

    private void loadNotificationTypes() {
        // if (Utils.isNetworkAvailable(SendNotification.this)) {

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "get", Utils.getNotificationType,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

        //} else {
        //   Utils.alertMessage(SendNotification.this, Utils.network_error);
        // }
    }

    private void loadRecipients() {
        //if (Utils.isNetworkAvailable(SendNotification.this)) {

        String timeStamp = Utils.getSharedPrefs(HomeActivity.this, HomeActivity.user.getId() + getResources().getString(R.string.activeUsersTimeStampStr));
        if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
            timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
        postDataParameters.put("timeStamp", timeStamp);

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "post", Utils.getActiveUserList,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

        // } else {
        // Utils.alertMessage(SendNotification.this, Utils.network_error);
        //}
    }
    private void getNotificationList() {

        // if (Utils.isNetworkAvailable(NotificationListActivity.this)) {

        String timeStamp = Utils.getSharedPrefs(HomeActivity.this, HomeActivity.user.getId() + getResources().getString(R.string.notificationTimeStampStr));
        if (timeStamp == null || timeStamp.equals(null) || timeStamp.equals("null"))
            timeStamp = "";

        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        postDataParameters.put("UserId", String.valueOf(HomeActivity.user.getId()));
        postDataParameters.put("timeStamp", timeStamp);

        Log.e("ss","ss"+postDataParameters.toString());
        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "post", Utils.GetNotificationAPI,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

        //} else {
        //  Utils.alertMessage(NotificationListActivity.this, Utils.network_error);
        // }
    }
    private void getUserList(int userId){

            String timeStamp = Utils.getSharedPrefs(HomeActivity.this, userId+getResources().getString(R.string.teamTimeStampStr));
            if(timeStamp==null || timeStamp.equals(null) || timeStamp.equals("null"))
                timeStamp="";

            HashMap<String, String> postDataParameters = new HashMap<String, String>();

            postDataParameters.put("TimeStamp",timeStamp);
            postDataParameters.put("UserId", String.valueOf(userId));

            Log.e("values", "userid: " + String.valueOf(userId) + ", TimeStamp: " + timeStamp);

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "post", Utils.getTeamAPI,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

    }

    private void GetUserListWorkSite(String userId){

        //int UserId = HomeActivity.user.getId();
        HashMap<String, String> postDataParameters = new HashMap<String, String>();
        postDataParameters.put("userid", userId);

        Log.e("" + Utils.GetUserListWorksiteActivity, "" + postDataParameters.toString());

        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                HomeActivity.this, "get", Utils.GetUserListWorksiteActivity,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
        mWebPageTask.execute();

    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            tab.setText("UserGraph");
            lay_indicator.setVisibility(View.INVISIBLE);
        } else if (tab.getPosition() == 1) {
            tab.setText("UserLoginGraph");
            lay_indicator.setVisibility(View.VISIBLE);
        }
       /* else if(tab.getPosition()==3)
        {
           // tab.setText("Tab3");
        }*/
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {

        private final ArrayList<TaskEntry> items;
        private final OnItemClickListener listener;
        private final boolean screen;

        public GraphAdapter(boolean screen, ArrayList<TaskEntry> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
            this.screen = screen;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = null;
            if (screen) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_slider_row, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_slider_two_row, parent, false);
            }
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

            private TextView graphBar, date;
            private TextView graphBar_signIn, graphBar_signOut;

            public ViewHolder(View itemView) {
                super(itemView);
                if (screen) {
                    graphBar = (TextView) itemView.findViewById(R.id.graphBar);
                    date = (TextView) itemView.findViewById(R.id.date);
                } else {
                    graphBar_signIn = (TextView) itemView.findViewById(R.id.graphBar_signIn);
                    graphBar_signOut = (TextView) itemView.findViewById(R.id.graphBar_signOut);
                    date = (TextView) itemView.findViewById(R.id.date);
                }
            }

            public void bind(final TaskEntry item, final OnItemClickListener listener, final int pos) {
                /*if(selectedPos==pos) {
                    date.setBackgroundResource(R.drawable.date_bg);
                    date.setTextColor(Color.WHITE);
                }else {
                    date.setBackgroundResource(R.drawable.date_bg_grey);
                    date.setTextColor(Color.BLACK);
                }*/
                if (screen) {
                    Double val = (graphBarHeight / maxValueTask) * item.getTimeSpent();

                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val.floatValue(), getResources().getDisplayMetrics());

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, height);
                    graphBar.setLayoutParams(param);
                    date.setText(item.getCreatedDate());
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        /*notifyItemChanged(pos);
                        selectedPos = pos;
                        notifyItemChanged(selectedPos);*/

                            listener.onItemClick(item, pos);
                        }
                    });
                } else {
                    Double valIn = (graphBarHeight / maxValueSignInOut) * item.getSignedInHours();
                    int heightIn = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valIn.floatValue(), getResources().getDisplayMetrics());

                    LinearLayout.LayoutParams paramIn = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, heightIn);
                    graphBar_signIn.setLayoutParams(paramIn);

                    Double val_signout = (graphBarHeight / maxValueSignInOut) * item.getSignedOutHours();
                    int heightOut = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val_signout.floatValue(), getResources().getDisplayMetrics());

                    LinearLayout.LayoutParams paramOut = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, heightOut);
                    graphBar_signOut.setLayoutParams(paramOut);

                    date.setText(item.getCreatedDate());
                   /* itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            *//*notifyItemChanged(pos);
                            selectedPos = pos;
                            notifyItemChanged(selectedPos);*//*

                            listener.onItemClick(item, pos);
                        }
                    });*/
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TaskEntry item, int position);
    }

    private void setViewPager() {
        // Pass results to ViewPagerAdapter Class
        ViewPagerAdapter adapter = new ViewPagerAdapter(HomeActivity.this, tabLayout.getTabCount());
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        //We set this on the indicator, NOT the pager
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                // tabLayout.getTabAt(position).select();

                tabLayout.setScrollPosition(position, 0f, true);
                if (position == 0) {
                    firstGraphView();
                    lay_indicator.setVisibility(View.INVISIBLE);

                }
                if (position == 1) {
                    secondGraphView();
                    lay_indicator.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }


    public class ViewPagerAdapter extends PagerAdapter {

        Context context;
        ArrayList<TimerTask> arrayList;
        LayoutInflater inflater;
        int value;
        int maxValue = 0;

        TextView scale0, scale1, scale2, scale3, scale4, scale5;

        public ViewPagerAdapter(Context context, int value) {
            this.context = context;
            this.arrayList = arrayList;
            this.value = value;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

//            if(position==0) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.viewpager_graphs, container, false);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.task_graph);
            recyclerView.setHasFixedSize(true);

            scale0 = (TextView) itemView.findViewById(R.id.scale0);
            scale1 = (TextView) itemView.findViewById(R.id.scale1);
            scale2 = (TextView) itemView.findViewById(R.id.scale2);
            scale3 = (TextView) itemView.findViewById(R.id.scale3);
            scale4 = (TextView) itemView.findViewById(R.id.scale4);
            scale5 = (TextView) itemView.findViewById(R.id.scale5);


            if (position == 0) {
                maxValue = maxValueTask.intValue();
                lay_indicator.setVisibility(View.INVISIBLE);
                //tabLayout.getTabAt(position).select();
                tabLayout.setScrollPosition(position, 0f, true);
                if(maxValue>4) {
                    maxValue = maxValue / 4;
                }
                maxValueTask = Double.valueOf(maxValue * 6);

                firstGraphView();
            } else {
                maxValue = maxValueSignInOut.intValue();
                if(maxValue>4) {
                    maxValue = maxValue / 4;
                }
                maxValueSignInOut = Double.valueOf(maxValue * 6);
            }


            scale0.setText(String.valueOf(0));
            scale1.setText(String.valueOf(1 * maxValue));
            scale2.setText(String.valueOf(2 * maxValue));
            scale3.setText(String.valueOf(3 * maxValue));
            scale4.setText(String.valueOf(4 * maxValue));
            scale5.setText(String.valueOf(5 * maxValue));

            ((ViewPager) container).addView(itemView);

            return itemView;
           /* }
            if(position==1) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemView = inflater.inflate(R.layout.viewpager_signinout, container, false);
                recyclerView = (RecyclerView)itemView.findViewById(R.id.task_graph);
                recyclerView.setHasFixedSize(true);
               // lay_indicator.setVisibility(View.VISIBLE);
                ((ViewPager) container).addView(itemView);

                return itemView;
            }

            return null;*/
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((LinearLayout) object);
        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        Log.e("" + methodName, "" + output);
        if (methodName.contains(Utils.UserTaskGraph)) {
            arrayList.addAll(parser.parseGraphsData(output));
            dbHandler.updateUserTask(arrayList);
            arrayList.clear();
            arrayList.addAll(dbHandler.getUserTask());

            ArrayList<TaskEntry> arrayList_sort = new ArrayList<>();
            arrayList_sort.addAll(arrayList);
            Comparator<TaskEntry> cmp = new Comparator<TaskEntry>() {
                @Override
                public int compare(TaskEntry v1, TaskEntry v2) {
                    return v1.getTimeSpent().compareTo(v2.getTimeSpent());
                }
            };
            TaskEntry taskEntry = Collections.max(arrayList_sort, cmp);
            maxValueTask = taskEntry.getTimeSpent();
            Log.e("max task: ", "" + maxValueTask);


//            setViewPager();
        } else if (methodName.contains(Utils.UsersGraph)) {
            arrayList_SignInOut.addAll(parser.parseGraphsSignInOut(output));
            dbHandler.updateUserSignInOut(arrayList_SignInOut);
            arrayList_SignInOut.clear();
            arrayList_SignInOut.addAll(dbHandler.getUserSignInOut());
            ArrayList<Double> signedInOutArray = new ArrayList<>();

            for (int i = 0; i < arrayList_SignInOut.size(); i++) {
                signedInOutArray.add(arrayList_SignInOut.get(i).getSignedInHours());
                signedInOutArray.add(arrayList_SignInOut.get(i).getSignedOutHours());
            }

            Comparator<Double> cmp = new Comparator<Double>() {
                @Override
                public int compare(Double v1, Double v2) {
                    return v1.compareTo(v2);
                }
            };

            maxValueSignInOut = Collections.max(signedInOutArray, cmp);

            Log.e("max sign in-out: ", "" + maxValueSignInOut);

            setViewPager();
        } else if (methodName.contains(Utils.Sync)) {


            SyncData syncData = parser.getSynOfflineData(output);
            syncUploadFile(syncData);// upload file

            // for delete offline task values
            int int_userId=getUserId();

            tasks_delete.clear();
            tasks_delete.addAll(dbHandler.getTaskEnteries(int_userId, "true", true));
            if (tasks_delete.size() >= 0) {
                for (int i = 0; i < tasks_delete.size(); i++) {
                    tasks_delete.get(i).setIsActive(false);
                }

                dbHandler.updateDeleteOffline(tasks_delete, "select date");


            }
            // for delete offline notification values
            notifications_delete.clear();
            // notifications_delete.addAll(dbHandler.getNotificationsByType("true",true));
            dbHandler.deleteNotificationOffline("true");

            syncDataCheck();//checking for offline data
            deleteIds.clear(); // delete task ids.
        } else if(methodName.equals(Utils.GetAssignedTaskList)){
            ArrayList<SpinnerData> assignedTasks = parser.parseAssignedProjects(output);

            dbHandler.updateProjectTasks(assignedTasks);//update data for notification type

            /*assignedTasks=dbHandler.getProjectTasksData();
            adapter = new SpinnerTypeAdapter(AddEditTaskEntry.this, R.layout.spinner_row_layout, assignedTasks);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
            spnProject.setAdapter(adapter);
            if(taskEntry!=null)
            {
                selectedSpinnerValue(spnProject);
            }*/
        } else if (methodName.equals(Utils.getTaskListAPI)) {
            String  UserId =   Utils.getSharedPrefs(getApplicationContext(),"apiUserId");
            ArrayList<TaskEntry> taskEntries = parser.parseTaskList(output, Integer.parseInt(UserId), "0");

            dbHandler.updateTask(taskEntries, "", false);

            //tasks = dbHandler.getTaskEnteries(UserId, selectedDate, false);
            //taskListview.setAdapter(new TaskAdapter(TaskListActivity.this));

        } else if (methodName.equals(Utils.getNotificationType)) {
            ArrayList<SpinnerData> notificationTypes = parser.parseNotificationType(output);
            dbHandler.updateNotificationType(notificationTypes);  //update data for notification type

            //notificationTypes=dbHandler.getNotificationTypeData();
            //adapter = new SpinnerTypeAdapter(SendNotification.this,
            //R.layout.spinner_row_layout, notificationTypes);
            //spnNotificationType.setAdapter(adapter); // Set the custom adapter to the spinner

        } else if (methodName.equals(Utils.getActiveUserList)) {
            ArrayList<User> userList = parser.parseActiveUsers(output);

            dbHandler.updateActiveUsers(userList);
            //userList = dbHandler.getActiveUsers();
        } else if(methodName.equals(Utils.getTeamAPI)) {
            Log.e("output", ",,, ::: " + output);
            ArrayList<User> teamMembers = parser.getTeamList(output, methodName);
            TimeEmDbHandler dbHandler = new TimeEmDbHandler(HomeActivity.this);
            dbHandler.updateTeam(teamMembers);

            String userid="";
            for(int i=0;i<teamMembers.size();i++) {
                userid=userid+teamMembers.get(i).getId();
                if(i != teamMembers.size()-1){
                    userid=userid+",";
                }
            }
            Log.e("StringGeneration",userid);
            GetUserListWorkSite(userid);

           // team = dbHandler.getTeam(HomeActivity.user.getId());
           // taskListview.setAdapter(new TeamAdapter(UserListActivity.this));
        }else if(methodName.contains(Utils.GetUserListWorksiteActivity)){

            parser = new Time_emJsonParser(HomeActivity.this);
            ArrayList<UserWorkSite> array_worksite = parser.getUserListWorkSite(output);
            Log.e("Array_Worksite",array_worksite.toString());

            dbHandler.deleteGeoGraphs();
            String userId="";
            for(int k=0;k<array_worksite.size();k++) {
                userId = array_worksite.get(k).getUserId();

                for (int i = 0; i < array_worksite.get(k).getArraylist_multiUserWorkSiteList().size(); i++) {
                    Gson gson = new Gson();
                    // This can be any object. Does not have to be an arraylist.
                    String allData = gson.toJson(array_worksite.get(k).getArraylist_multiUserWorkSiteList().get(i).getArraylist_WorkSiteList());
                    String str_Date=array_worksite.get(k).getArraylist_multiUserWorkSiteList().get(i).getDate();
                    dbHandler.updateGeoGraphData(userId, allData,str_Date );

                }
            }

        }
        else if(methodName.equalsIgnoreCase(Utils.GetNotificationAPI))
        {
            notifications = parser.parseNotificationList(output);
            dbHandler.updateNotifications(notifications);
        }

    }

    private void syncDataCheck() {


        imageSync.setImageDrawable(getResources().getDrawable(R.drawable.sync_green));
        TimeEmDbHandler dbHandler = new TimeEmDbHandler(HomeActivity.this);
        //for notification
        notifications.clear();
        notifications.addAll(dbHandler.getNotificationsByType("true", true, "true"));
        Log.e("notification size", "" + notifications.size());
        //for delete notification
        if (notifications != null && notifications.size() > 0) {
            imageSync.setImageDrawable(getResources().getDrawable(R.drawable.sync_red));
        }
       int userId=getUserId();
        tasks.clear();
        tasks.addAll(dbHandler.getTaskEnteries(userId, "true", true));
        Log.e("task size", "" + tasks.size());
        // for delete task
        if (tasks != null && tasks.size() > 0) {
            imageSync.setImageDrawable(getResources().getDrawable(R.drawable.sync_red));
        }
        if (deleteIds.size() > 0) {
            imageSync.setImageDrawable(getResources().getDrawable(R.drawable.sync_red));
        }
    }

    private void syncUploadData() {
        int int_userId=getUserId();
        TimeEmDbHandler dbHandler = new TimeEmDbHandler(HomeActivity.this);

        //for notification
        notifications.clear();
        notifications.addAll(dbHandler.getNotificationsByType("true", true, "true"));
        Log.e("notification size", "" + notifications.size());


        //for task
        tasks.clear();
        tasks.addAll(dbHandler.getTaskEnteries(int_userId, "true", true));
        if(tasks.size()>0 | deleteIds.size()>0 | notifications.size()>0) {
            syncUploadAPI(tasks, deleteIds, notifications);
        }else{
            Utils.showToast(HomeActivity.this,"No offline data available to sync.");
        }
        Log.e("task size", "" + tasks.size());

    }

    private void syncUploadAPI(ArrayList<TaskEntry> tasks, ArrayList<String> deleteIds, ArrayList<Notification> notifications) {

        // for task
        ArrayList<HashMap<String, String>> arrayHashMap = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("CreatedDate", tasks.get(i).getCreatedDate());
            parameters.put("Comments", "" + tasks.get(i).getComments());
            parameters.put("UserId", "" + tasks.get(i).getUserId());
            parameters.put("TaskId", "" + tasks.get(i).getTaskId());
            parameters.put("ActivityId", "" + tasks.get(i).getActivityId());
            parameters.put("TimeSpent", "" + tasks.get(i).getTimeSpent());
            parameters.put("Id", "" + tasks.get(i).getId());

            Log.e("getAttachmentImageFile", "" + tasks.get(i).getAttachmentImageFile());
            String value = tasks.get(i).getAttachmentImageFile();

            if (value != null && !value.equalsIgnoreCase("null"))
                parameters.put("UniqueNumber", String.valueOf(tasks.get(i).getUniqueNumber()));
            else
                parameters.put("UniqueNumber", "null");


            if (tasks.get(i).getId() == 0)
                parameters.put("Operation", "add");
            else
                parameters.put("Operation", "update");
            arrayHashMap.add(parameters);
        }
        for (int i = 0; i < deleteIds.size(); i++) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("Id", "" + deleteIds.get(i));
            parameters.put("Operation", "" + "delete");
            arrayHashMap.add(parameters);
        }

 		/*for notification*/
        ArrayList<HashMap<String, String>> array_HashMapNotification = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("Subject", notifications.get(i).getSubject());
            parameters.put("Message", notifications.get(i).getMessage());
            String string = notifications.get(i).getCreatedDate();
            String date = string.replace("/", "-");
            //parameters.put("CreatedDate", date);
            parameters.put("NotificationTypeId", String.valueOf(notifications.get(i).getNotificationTypeId()));
            parameters.put("UniqueNumber", notifications.get(i).getUniqueNumber());
            parameters.put("UserId", String.valueOf(notifications.get(i).getUserId()));
            parameters.put("NotifyTo", String.valueOf(notifications.get(i).getSenderId()));
            array_HashMapNotification.add(parameters);
        }
        Log.e("task hash map", "" + arrayHashMap.toString());
        Log.e("notification hash map", "" + array_HashMapNotification.toString());
        if (Utils.isNetworkAvailable(HomeActivity.this)) {

            JSONArray jsonArray = null, jsonArray_NotificationData = null;
            JSONObject jsonObject = null, jsonObject_notification = null;
            try {
                jsonArray = new JSONArray(arrayHashMap.toString().replace(" ", ""));
                jsonObject = new JSONObject();
                jsonObject.put("userTaskActivities", jsonArray);
                jsonArray_NotificationData = new JSONArray(array_HashMapNotification.toString().replace(" ", ""));
                jsonObject.put("notifications", jsonArray_NotificationData);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, "Someting Wrong, try again", Toast.LENGTH_LONG).show();
            }


            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("Data", "" + jsonObject.toString());
            Log.e("" + Utils.Sync, "" + postDataParameters.toString());

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "post", Utils.Sync,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessageWithoutBack(HomeActivity.this, Utils.network_error);
        }
    }

    private void syncUploadFile(SyncData syncData) {

        String ImagePath = null;

        //for task file upload
        for (int i = 0; i < syncData.getArray_taks().size(); i++) {

            ArrayList<MultipartDataModel> dataModels = new ArrayList<>();
            dataModels.add(new MultipartDataModel("Id",
                    String.valueOf(syncData.getArray_taks().get(i).getId()), MultipartDataModel.STRING_TYPE));
            dataModels.add(new MultipartDataModel("FileUploadFor", "usertaskactivity", MultipartDataModel.STRING_TYPE));

            ImagePath = "";
            for (int j = 0; j < tasks.size(); j++) {

                //   Log.e("parser un",""+syncData.getArray_taks().get(i).getUniqueNumber());
                //    Log.e("task un",""+tasks.get(j).getUniqueNumber());

                if (syncData.getArray_taks().get(i).getUniqueNumber()
                        .equalsIgnoreCase(tasks.get(j).getUniqueNumber())) {
                    ImagePath = tasks.get(j).getAttachmentImageFile();
                }
            }
            if (ImagePath != null)
                dataModels.add(new MultipartDataModel("profile_picture", ImagePath, MultipartDataModel.FILE_TYPE));
            Log.e("send task", "send task" + ImagePath);

            fileUtils.sendMultipartRequest(Utils.SyncFileUpload, dataModels);
        }

        //for notification file upload
        for (int i = 0; i < syncData.getArray_noitification().size(); i++) {

            ArrayList<MultipartDataModel> dataModels = new ArrayList<>();
            dataModels.add(new MultipartDataModel("Id",
                    String.valueOf(syncData.getArray_noitification().get(i).getNotificationId()), MultipartDataModel.STRING_TYPE));
            dataModels.add(new MultipartDataModel("FileUploadFor", "notification", MultipartDataModel.STRING_TYPE));
            for (int j = 0; j < notifications.size(); j++) {

                // Log.e("parser un",""+syncData.getArray_noitification().get(i).getUniqueNumber());
                // Log.e("task un",""+notifications.get(j).getUniqueNumber());

                if (syncData.getArray_noitification().get(i).getUniqueNumber()
                        .equalsIgnoreCase(notifications.get(j).getUniqueNumber())) {
                    ImagePath = notifications.get(j).getAttachmentPath();
                }
            }
            if
                    (ImagePath != null)
                dataModels.add(new MultipartDataModel("profile_picture", ImagePath, MultipartDataModel.FILE_TYPE));

            Log.e("send notification", "send notification" + ImagePath);
            fileUtils.sendMultipartRequest(Utils.SyncFileUpload, dataModels);

        }

    }
    public static void startLocationService(Context context)
    {
        //start services
        stopLocationService(context);
        context.startService(new Intent(context,BackgroundLocationService.class));
    }
    public static void stopLocationService(Context context)
    {
        //stop services
        context.stopService(new Intent(context,BackgroundLocationService.class));
    }
    private void addWidget()
    {
        Home_arrayList_widget=new ArrayList<>();
        Home_arrayList_widget.clear();
        AddWigdetView.removeAllViews();
        AddWigdetView.setPadding(5,0,5,0);
        Home_arrayList_widget=Utils.getWidget(HomeActivity.this);
        if(Home_arrayList_widget!=null && Home_arrayList_widget.size()>0) {
           for(int i=0;i<Home_arrayList_widget.size();i++) {
               try {
                   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   View rowView = inflater.inflate(R.layout.template_add_widget_view, null);

                   CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
                   checkBox.setVisibility(View.GONE);

                   TextView textView_name = (TextView) rowView.findViewById(R.id.textView_name);
                   textView_name.setText(Home_arrayList_widget.get(i).getName());
                   textView_name.setTextColor(getResources().getColor(R.color.white));

                   RelativeLayout relativeLayout=(RelativeLayout)rowView.findViewById(R.id.SingleGrideView);
                   relativeLayout.setBackgroundColor(Color.parseColor(Home_arrayList_widget.get(i).getColor()));
                   relativeLayout.setPadding(5,0,5,0);
                   AddWigdetView.addView(rowView);
               }catch(Exception e)
               {
                   e.printStackTrace();
               }

            }
        }

    }
private int getUserId()
{
    int int_userId=0;
    String userId=Utils.getSharedPrefs(getApplicationContext(),"apiUserId");
    try{
        int_userId=Integer.parseInt(userId);
        return int_userId;
    }catch(Exception e)
    { e.printStackTrace();   }
    return int_userId;
}

}
 /*  private void addGraph() {
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
    }*/

    /*private void populatRecyclerView(ArrayList<TaskEntry> arraylist) {

        arrayList.addAll(arraylist);
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
    }*/