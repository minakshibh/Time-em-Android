package com.time_em.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.Company;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.PrefUtils;
import com.time_em.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;


public class CompanyListActivity extends Activity implements AsyncResponseTimeEm {


    private ImageView back,editButton;
    private TextView headerText,txtNext;
    private ListView listView;
    private Time_emJsonParser parser;
    private ArrayList<Company> arrayList_company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companylist);

        initScreen();

        getCompanyList();
        setOnItemClickListener();
    }



    private void initScreen() {
        editButton = (ImageView) findViewById(R.id.AddButton);
        editButton.setVisibility(View.INVISIBLE);
        editButton.setImageDrawable(getResources().getDrawable(R.drawable.edit));
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);
        headerText=(TextView) findViewById(R.id.headerText);
        headerText.setTextSize(25);
        headerText.setText("Choose Company");
        listView=(ListView)findViewById(R.id.listView);

    }
    private void setOnItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //save value for company key
                PrefUtils.setStringPreference(CompanyListActivity.this,PrefUtils.KEY_COMPANY,arrayList_company.get(position).getKey());

                Intent mIntent=new Intent(CompanyListActivity.this, HomeActivity.class);
                mIntent.putExtra("trigger", getIntent().getStringExtra("trigger"));
                startActivity(mIntent);
                finish();
            }
        });
    }

    private void getCompanyList()
    {
        HashMap<String, String> postDataParameters = new HashMap<String, String>();

        String userId=Utils.getSharedPrefs(getApplicationContext(), PrefUtils.KEY_USER_ID);
        postDataParameters.put("userId", String.valueOf(userId));

        Log.e(Utils.GetUserCompaniesList,""+postDataParameters.toString());
        AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                CompanyListActivity.this, "get", Utils.GetUserCompaniesList,
                postDataParameters, true, "Please wait...");
        mWebPageTask.delegate = (AsyncResponseTimeEm) CompanyListActivity.this;
        mWebPageTask.execute();
    }
    public class ListAdapter extends BaseAdapter {
        private Context context;
        private TextView name;


        public ListAdapter(Context ctx) {
            context = ctx;
        }

        // @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayList_company.size();
        }

        // @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrayList_company.get(position);
        }

        // @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        // @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.company_row, parent,
                        false);
            }

            name = (TextView) convertView.findViewById(R.id.textView_name);
            name.setText(arrayList_company.get(position).getValue());


            return convertView;
        }
    }
    @Override
    public void processFinish(String output, String methodName) {
        if(methodName.equalsIgnoreCase(Utils.GetUserCompaniesList)){
            arrayList_company=new ArrayList<>();
            parser=new Time_emJsonParser(CompanyListActivity.this);
            arrayList_company=parser.parseCompanyList(output);
            Log.e("",output);
            listView.setAdapter(new ListAdapter(CompanyListActivity.this));
        }

    }
}