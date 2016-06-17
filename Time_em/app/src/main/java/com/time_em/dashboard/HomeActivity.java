package com.time_em.dashboard;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimerTask;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.Notification;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.tasks.TaskListActivity;
import com.time_em.utils.GcmUtils;
import com.time_em.utils.Utils;

public class HomeActivity extends BaseActivity implements AsyncResponseTimeEm,TabLayout.OnTabSelectedListener {

    private LinearLayout graphLayout,lay_indicator;
    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    BarDataSet dataset;
    public DependencyResolver resolver;
    ArrayList<String> labels = new ArrayList<String>();
    public static User user;
    private LinearLayout changeStatus;
    private String trigger;
    private ImageView userStatus, imgStatus;
    private TextView txtUserStatus;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private Context context;
    ArrayList<TaskEntry> arrayList=new ArrayList<>();
    ArrayList<TaskEntry> arrayList_SignInOut=new ArrayList<>();
    private ArrayList<Notification> notifications=new ArrayList<>();
    private ArrayList<TaskEntry> tasks=new ArrayList<>();
    private Time_emJsonParser parser;
    private Double  maxValueTask=0.0,maxValueSignIn=0.0,maxValueSignOut=0.0;
    private TextView currentDate;
    TabLayout  tabLayout;

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
      //  populatRecyclerView();
        registerDevice();
        fetchTaskGraphsData();
        fetchGraphsSignInOut();
        initScreen();
        setClickListeners();
        setTapBar();
        getCurrentDate();

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

    private void initScreen() {

        viewPager=(ViewPager)findViewById(R.id.pager);
        changeStatus = (LinearLayout) findViewById(R.id.changeStatus);
        userStatus = (ImageView) findViewById(R.id.userStatus);
        txtUserStatus = (TextView) findViewById(R.id.txtUserStatus);
        imgStatus = (ImageView) findViewById(R.id.imgStatus);
        trigger = getIntent().getStringExtra("trigger");
        lay_indicator=(LinearLayout)findViewById(R.id.lay_indicator);
        currentDate=(TextView)findViewById(R.id.currentDate);
         parser = new Time_emJsonParser(HomeActivity.this);
        if (user.getUserTypeId() == 4)
            myTeam.setVisibility(View.GONE);


    }
private void getCurrentDate()
{
    long date = System.currentTimeMillis();

    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM, yyyy");
    String dateString = sdf.format(date);
    currentDate.setText(dateString);
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
        syncDataCheck();
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
    private void fetchTaskGraphsData(){
        if (Utils.isNetworkAvailable(HomeActivity.this)) {


            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("userid", ""+user.getId());

            //http://timeemapi.azurewebsites.net/api/usertask/UserTaskGraph?userid=2
            Log.e(Utils.UserTaskGraph,postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "get", Utils.UserTaskGraph,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }
    private void fetchGraphsSignInOut(){
        if (Utils.isNetworkAvailable(HomeActivity.this)) {


            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("userid", ""+user.getId());

           // http://timeemapi.azurewebsites.net/api/usertask/UsersGraph?userid=2
            Log.e(Utils.UsersGraph,postDataParameters.toString());
            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    HomeActivity.this, "get", Utils.UsersGraph,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) HomeActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(HomeActivity.this, Utils.network_error);
        }
    }
    private void firstGraphView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GraphAdapter  adapter = new GraphAdapter(true,arrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(TaskEntry item, int position) {

                Utils.showToast(HomeActivity.this, item.getCreatedDate() +" Clicked");

            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();
    }
    private void secondGraphView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        GraphAdapter  adapter = new GraphAdapter(false,arrayList_SignInOut, new OnItemClickListener() {
            @Override
            public void onItemClick(TaskEntry item, int position) {

                Utils.showToast(HomeActivity.this, item.getCreatedDate() +" Clicked");

            }
        });
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        viewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition()==0)
        {
            tab.setText("UserGraph");
            lay_indicator.setVisibility(View.GONE);
            }
        else if(tab.getPosition()==1)
        {
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

        public GraphAdapter(boolean screen ,ArrayList<TaskEntry> items, OnItemClickListener listener) {
            this.items = items;
            this.listener=listener;
            this.screen = screen;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=null;
            if(screen) {
                 v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_slider_row, parent, false);
            }else{
                 v = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_slider_two_row, parent, false);
            }
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
            private TextView graphBar_signIn, graphBar_signOut;
            public ViewHolder(View itemView) {
                super(itemView);
                if (screen) {
                    graphBar = (TextView) itemView.findViewById(R.id.graphBar);
                    date = (TextView) itemView.findViewById(R.id.date);
                }else
                {
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
                    Double val = (200 / maxValueTask) * item.getTimeSpent();
                    RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, val.intValue());
                    param.setMargins(10, 10, 10, 0);
                    param.addRule(RelativeLayout.ABOVE, date.getId());
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
                }

            else{
                    Double valIn = (200 / maxValueSignIn) * item.getSignedInHours();
                    LinearLayout.LayoutParams paramIn = new LinearLayout.LayoutParams(
                            0, valIn.intValue(),1);
                    paramIn.setMargins(0, 0, 0, 0);
                    paramIn.gravity = Gravity.BOTTOM;
                    graphBar_signIn.setLayoutParams(paramIn);
                    graphBar_signIn.setGravity(Gravity.BOTTOM);

                    Double val_signout = (200 / maxValueSignOut) * item.getSignedOutHours();
                    LinearLayout.LayoutParams paramOut = new LinearLayout.LayoutParams(
                            0, val_signout.intValue(),1);
                    paramOut.setMargins(2, 0, 0, 0);
                    paramOut.gravity = Gravity.BOTTOM;
                    graphBar_signOut.setLayoutParams(paramOut);
                    graphBar_signOut.setGravity(Gravity.BOTTOM);

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
        ViewPagerAdapter  adapter = new ViewPagerAdapter(HomeActivity.this,tabLayout.getTabCount());
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        //We set this on the indicator, NOT the pager
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

               // tabLayout.getTabAt(position).select();

                tabLayout.setScrollPosition(position,0f,true);
                if (position == 0) {
                    firstGraphView();
                    lay_indicator.setVisibility(View.GONE);

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

        public ViewPagerAdapter(Context context, int value) {
            this.context = context;
            this.arrayList = arrayList;
            this.value=value;
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

            if(position==0) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemView = inflater.inflate(R.layout.viewpager_graphs, container, false);
                recyclerView = (RecyclerView)itemView.findViewById(R.id.task_graph);
                recyclerView.setHasFixedSize(true);
                firstGraphView();
                lay_indicator.setVisibility(View.GONE);
                //tabLayout.getTabAt(position).select();
                tabLayout.setScrollPosition(position,0f,true);
                ((ViewPager) container).addView(itemView);

                return itemView;
            }
            if(position==1) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemView = inflater.inflate(R.layout.viewpager_signinout, container, false);
                recyclerView = (RecyclerView)itemView.findViewById(R.id.task_graph);
                recyclerView.setHasFixedSize(true);
               // lay_indicator.setVisibility(View.VISIBLE);
                ((ViewPager) container).addView(itemView);

                return itemView;
            }

            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((LinearLayout) object);

        }
    }

    @Override
    public void processFinish(String output, String methodName) {
        Log.e(""+methodName,""+output);
        if(methodName.contains(Utils.UserTaskGraph))
        {
            arrayList.addAll(parser.parseGraphsData(output));
            setViewPager();
            ArrayList<TaskEntry> arrayList_sort=new ArrayList<>();
            arrayList_sort.addAll(arrayList);
              Comparator<TaskEntry> cmp = new Comparator<TaskEntry>() {
                @Override
                public int compare(TaskEntry v1, TaskEntry v2) {
                    return v1.getTimeSpent().compareTo(v2.getTimeSpent());
                }
            };
            TaskEntry taskEntry=Collections.max(arrayList_sort, cmp);
            maxValueTask=taskEntry.getTimeSpent();
            Log.e("max task: " ,""+ maxValueTask);


            }
      else if(methodName.contains(Utils.UsersGraph))
        {
            arrayList_SignInOut.addAll(parser.parseGraphsSignInOut(output));
            setViewPager();

            ArrayList<TaskEntry> arrayList_SignIn_sort=new ArrayList<>();
            arrayList_SignIn_sort.addAll(arrayList_SignInOut);

            Comparator<TaskEntry> cmp = new Comparator<TaskEntry>() {
                @Override
                public int compare(TaskEntry v1, TaskEntry v2) {
                    return v1.getSignedInHours().compareTo(v2.getSignedInHours());
                }
            };
            TaskEntry taskEntry=Collections.max(arrayList_SignIn_sort, cmp);
            maxValueSignIn=taskEntry.getSignedInHours();
            Log.e("max sign In: " ,""+ maxValueSignIn);
            ///sign out max

            ArrayList<TaskEntry> arrayList_SignOut_sort=new ArrayList<>();
            arrayList_SignOut_sort.addAll(arrayList_SignInOut);

            Comparator<TaskEntry> cmp_out = new Comparator<TaskEntry>() {
                @Override
                public int compare(TaskEntry v1, TaskEntry v2) {
                    return v1.getSignedOutHours().compareTo(v2.getSignedOutHours());
                }
            };
            TaskEntry taskEntry2=Collections.max(arrayList_SignOut_sort, cmp);
            maxValueSignIn=taskEntry2.getSignedOutHours();
            Log.e("max sign out: " ,""+ maxValueSignIn);
        }
    }
    private void syncDataCheck() {
        imageSync.setImageDrawable(getResources().getDrawable(R.drawable.online));
        TimeEmDbHandler dbHandler = new TimeEmDbHandler(HomeActivity.this);
        //for notification
        notifications.clear();
        notifications.addAll(dbHandler.getNotificationsByType("true", true));
        Log.e("notification size", "" + notifications.size());
        //for delete notification
        if (notifications != null && notifications.size() > 0) {
            imageSync.setImageDrawable(getResources().getDrawable(R.drawable.offline));
        }
        tasks.clear();
        tasks.addAll(dbHandler.getTaskEnteries(HomeActivity.user.getId(),"true",true));
        Log.e("task size", "" + tasks.size());
        // for delete task
        if (notifications != null && notifications.size() > 0) {
            imageSync.setImageDrawable(getResources().getDrawable(R.drawable.offline));
        }
    }
}
