package com.time_em.barcode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.time_em.android.R;
import com.time_em.asynctasks.AsyncResponseTimeEm;
import com.time_em.asynctasks.AsyncTaskTimeEm;
import com.time_em.db.TimeEmDbHandler;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;
import com.time_em.utils.Utils;
import net.sourceforge.zbar.ImageScanner;
import java.util.ArrayList;
import java.util.HashMap;


public class BarcodeScanActivity extends Activity implements AsyncResponseTimeEm {


    private boolean refresh;
    private long barCode=0;
    private String nfcCode="";

    public static ArrayList<String> arrayList_scanCode = new ArrayList<>();
    private ArrayList<User> arrayListUsers = new ArrayList<>();
    private ArrayList<User> aL_UsersExistDb = new ArrayList<>();
    private ArrayList<User> aL_UsersNotExistDb = new ArrayList<>();
    //private ArrayList<User> refreshArrayListUsers = new ArrayList<>();

    private TimeEmDbHandler dbHandler;
    private User user = new User();
    private ListAdapter adapter;
    private Time_emJsonParser parser;

    private RelativeLayout lay_listView;
    private ListView listView;
    private ImageScanner scanner;
    private FrameLayout preview;
    private TextView headerText;
    private TextView btn_signIn,btn_signOut;
    private ImageView back, AddButton;
    private String trigger="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);


        initUI();
        setOnClickListener();
        if (getIntent().getStringExtra("trigger") != null)
        {
            trigger=getIntent().getStringExtra("trigger");
             fetchUserByBarCode();
             setAdapter(aL_UsersExistDb,aL_UsersNotExistDb);
            }
    }


    private void initUI() {

        headerText=(TextView)findViewById(R.id.headerText);
        headerText.setText("Scanned Users");
        back=(ImageView)findViewById(R.id.back);
        AddButton=(ImageView)findViewById(R.id.AddButton);
        AddButton.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listView);
        btn_signIn=(TextView)findViewById(R.id.btn_signIn);
        btn_signOut=(TextView)findViewById(R.id.btn_signOut);
        lay_listView = (RelativeLayout) findViewById(R.id.lay_listView);
        lay_listView.setVisibility(View.VISIBLE);
       // scanButton = (Button) findViewById(R.id.ScanButton);
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.setVisibility(View.GONE);

    }
    private void setOnClickListener() {
        back.setOnClickListener(Listener);
        btn_signIn.setOnClickListener(Listener);
        btn_signOut.setOnClickListener(Listener);
       }
    public View.OnClickListener Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == back) {
                //releaseCamera();
                finish();

            }else if (v == btn_signIn) {
                String Ids=getAllUsersIds(arrayListUsers);
                Log.e("Ids=", Ids);
             if(Ids!=null && !Ids.equals("")) {
                Utils.ChangeStatus(BarcodeScanActivity.this, ""+Ids,"signIn");
            }else{
                Utils.showToast(getApplicationContext(),"No record for scanned users");
            }

            }else if (v == btn_signOut) {

                String Ids=getAllUsersIds(arrayListUsers);
                Log.e("Ids=", Ids);
                if(Ids!=null && !Ids.equals("")) {
                    Utils.ChangeStatus(BarcodeScanActivity.this, "" + Ids, "SignOut");
                }else{
                    Utils.showToast(getApplicationContext(),"No record for scanned users");
                }
            }

        }
    };

    private void setAdapter(ArrayList<User> aL_UsersExistDb,ArrayList<User> aL_UsersNotExistDb) {
            arrayListUsers.clear();
        if (aL_UsersExistDb.size() >0) {
            arrayListUsers.addAll(aL_UsersExistDb);
        }
        if (aL_UsersNotExistDb.size() >0) {

            arrayListUsers.addAll(aL_UsersNotExistDb);
        }

        adapter = new ListAdapter(arrayListUsers, getApplicationContext());
        listView.setAdapter(adapter);



    }
    private void fetchUserByBarCode() {
        dbHandler = new TimeEmDbHandler(getApplicationContext());

        if(arrayList_scanCode!=null && arrayList_scanCode.size()>0) {
            for (int i = 0; i < arrayList_scanCode.size(); i++) {
                try {
                    if(trigger.equalsIgnoreCase("nfc")) {

                        nfcCode=arrayList_scanCode.get(i).trim();
                        user = dbHandler.getTeamByLoginCode(0,nfcCode,"nfc");
                    }else{
                        barCode = Long.parseLong(arrayList_scanCode.get(i).trim());
                        user = dbHandler.getTeamByLoginCode(barCode,null,"barcode");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Try again..", Toast.LENGTH_LONG).show();
                }

                if (user == null) {
                    refresh = false;
                  //  Toast.makeText(getApplicationContext(), "User not found " + barCode, Toast.LENGTH_LONG).show();
                   if(trigger.equalsIgnoreCase("nfc"))
                   {
                       getUserDetailsNFC(nfcCode);
                   }else {
                       getUserDetails(barCode);
                   }
                }
                else {
                    aL_UsersExistDb.add(user);
                }
            }
        }
      }
    private String getAllUsersIds(ArrayList<User> arrayList){
       String strUserIds="";
        if(arrayList!=null && arrayList.size()!=0)
        {
            for(int i=0;i<arrayList.size();i++)
            {
                if(i==0)
                {
                    strUserIds=""+arrayList.get(i).getId();
                    }
                else{
                    strUserIds=strUserIds+","+arrayList.get(i).getId();
                     }
                }
            }
        return strUserIds;
    }

    private void getUserDetails(long userCode){

       // if (Utils.isNetworkAvailable(BarcodeScanActivity.this)) {
        //http://timeemapi.azurewebsites.net/api/User/GetUsersListByLoginCode?Logincode=9105
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("Logincode", String.valueOf(userCode));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    BarcodeScanActivity.this, "get", Utils.GetUsersListByLoginCode,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) BarcodeScanActivity.this;
            mWebPageTask.execute();

        //} else {
           // Utils.alertMessage(BarcodeScanActivity.this, Utils.network_error);
       // }
    }
    private void getUserDetailsNFC(String nfcTagId){

      //  if (Utils.isNetworkAvailable(BarcodeScanActivity.this)) {
           // http://timeemapi.azurewebsites.net/api/User/GetUsersListByTagId
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("NFCTagId",nfcTagId);//"0404b672973c81"


            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    BarcodeScanActivity.this, "get", Utils.GetUsersListByTagId,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) BarcodeScanActivity.this;
            mWebPageTask.execute();

       /* } else {
            Utils.alertMessage(BarcodeScanActivity.this, Utils.network_error);
        }*/
    }

    public class ListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<User> arrayList;
        private User user;
        private LayoutInflater inflater;

        public ListAdapter(ArrayList<User> arrayList, Context mContext) {
            this.context = mContext;
            this.arrayList = arrayList;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.usercode_row, parent, false);
                holder = new ViewHolder();

                holder.userName = (TextView) convertView.findViewById(R.id.userName);
                holder.shift = (ImageView) convertView.findViewById(R.id.shiftInfo);
                holder.status = (ImageView) convertView.findViewById(R.id.status);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            user = arrayList.get(position);
            holder.userName.setText(user.getFullName());

            if (user.isSignedIn()) {
                holder.status.setImageResource(R.drawable.online);

            } else {
                holder.status.setImageResource(R.drawable.offline);

            }

            if (user.isNightShift())
                holder.shift.setImageResource(R.drawable.night);
            else
                holder.shift.setImageResource(R.drawable.day);

              return convertView;

        }

        class ViewHolder {
            TextView userName;// signInInfo, signOutInfo;
            ImageView status, shift;
        }
    }
    @Override
    public void processFinish(String output, String methodName) {
        parser = new Time_emJsonParser(BarcodeScanActivity.this);


        if(methodName.contains(Utils.GetUsersListByLoginCode)) {
            ArrayList<User> teamMembers=new ArrayList<>();
            try {
                teamMembers = parser.getTeamList(output, methodName);
            }catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "Invalid user,Please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            boolean result=true;
            if(aL_UsersNotExistDb!=null && aL_UsersNotExistDb.size()>0) {
                for(int i=0;i<aL_UsersNotExistDb.size();i++) {
                    if (aL_UsersNotExistDb.get(i).getId()==teamMembers.get(0).getId())
                    {
                       result= false;
                        }
                }

            }
            if(result)
                aL_UsersNotExistDb.addAll(teamMembers);
                setAdapter(aL_UsersExistDb, aL_UsersNotExistDb);

             }
        else if(methodName.contains(Utils.GetUsersListByTagId))
        {
            ArrayList<User> teamMembers=new ArrayList<>();
            try {
                teamMembers = parser.getTeamList(output, methodName);
            }catch(Exception e)
            {
                e.printStackTrace(); }
            boolean result=true;
            if(aL_UsersNotExistDb!=null && aL_UsersNotExistDb.size()>0) {
                for(int i=0;i<aL_UsersNotExistDb.size();i++) {
                    if (aL_UsersNotExistDb.get(i).getId()==teamMembers.get(0).getId())
                    {
                        result= false;
                    }
                }

            }
                if(result)
                aL_UsersNotExistDb.addAll(teamMembers);
                setAdapter(aL_UsersExistDb, aL_UsersNotExistDb);

        }


         else if(methodName.contains(Utils.SignInByUserId)){
            aL_UsersExistDb.clear();
            ArrayList<User> teamMembers = parser.parseSignInChangeStatusResponse(output, methodName);

                     if(teamMembers!=null && teamMembers.size()>0) {
                         TimeEmDbHandler dbHandler = new TimeEmDbHandler(BarcodeScanActivity.this);
                         for (int i = 0; i < teamMembers.size(); i++) {
                             dbHandler.updateStatus(teamMembers.get(i).getId(), "" + teamMembers.get(i).getActivityId()
                                     , teamMembers.get(i).getSignInAt(), "", true);
                         }

                         // setAdapter();
                         for (int i = 0; i < arrayListUsers.size(); i++) {
                             try {
                                 if(trigger.equalsIgnoreCase("nfc")) {
                                     nfcCode = arrayListUsers.get(i).getNfcTagId();
                                     user = dbHandler.getTeamByLoginCode(0,nfcCode, "nfc");
                                 }else {
                                     barCode = Long.parseLong(arrayListUsers.get(i).getLoginCode());
                                     user = dbHandler.getTeamByLoginCode(barCode, null, "barcode");
                                 }
                             } catch (Exception e) {
                                 e.printStackTrace();
                                 Toast.makeText(getApplicationContext(), "Invalid user,Please try again.", Toast.LENGTH_LONG).show();
                             }

                             if (user == null) {
                              if(aL_UsersNotExistDb!=null && aL_UsersNotExistDb.size()>0)
                                 {
                                     for(int j=0;j<aL_UsersNotExistDb.size();j++){
                                         User getUser=aL_UsersNotExistDb.get(j);
                                         getUser.setSignedIn(true);
                                        }
                                    }

                             }
                             else {
                                 aL_UsersExistDb.add(user);

                             }

                         }

                       setAdapter(aL_UsersExistDb,aL_UsersNotExistDb);
                     }
                  else {
                         // Utils.alertMessage(BarcodeScanActivity.this, " User already Signed In.");
                     }
            goToCameraView();

              }
          else if(methodName.contains(Utils.SignOutByUserId)){
            aL_UsersExistDb.clear();
            ArrayList<User> teamMembers = parser.parseSignOutChangeStatusResponse(output, methodName);
            if(teamMembers!=null && teamMembers.size()>0) {
                TimeEmDbHandler dbHandler = new TimeEmDbHandler(BarcodeScanActivity.this);
                for (int i = 0; i < teamMembers.size(); i++) {
                    dbHandler.updateStatus(teamMembers.get(i).getId(), "" + teamMembers.get(i).getActivityId(),
                            teamMembers.get(i).getSignInAt()  , teamMembers.get(i).getSignOutAt(), false);
                }
                for(int i=0;i<arrayListUsers.size();i++)
                {
                    try {
                        if(trigger.equalsIgnoreCase("nfc")) {
                            nfcCode = arrayListUsers.get(i).getNfcTagId();
                            user = dbHandler.getTeamByLoginCode(0,nfcCode, "nfc");
                        }else {
                            barCode = Long.parseLong(arrayListUsers.get(i).getLoginCode());
                            user = dbHandler.getTeamByLoginCode(barCode, null, "barcode");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Invalid user,Please try again.",Toast.LENGTH_LONG).show();
                    }

                    if(user==null) {
                      if(aL_UsersNotExistDb!=null && aL_UsersNotExistDb.size()>0)
                        {
                            for(int j=0;j<aL_UsersNotExistDb.size();j++){
                               User getUser=aL_UsersNotExistDb.get(j);
                                getUser.setSignedIn(true);
                            }
                        }

                    }
                    else {
                        aL_UsersExistDb.add(user);

                    }

                }
               setAdapter(aL_UsersExistDb,aL_UsersNotExistDb);

            }
                  else{
                     // Utils.alertMessage(BarcodeScanActivity.this, " User already Signed Out.");

                    }

                goToCameraView();



            }


        }
    private void goToCameraView()
    {
         if(trigger.equalsIgnoreCase("barcode")) {
           /* Intent mIntent = new Intent(BarcodeScanActivity.this, CameraOpenActivity.class);
            startActivity(mIntent);*/
            finish();
            }
        else{
                finish();
            }
        }


}

