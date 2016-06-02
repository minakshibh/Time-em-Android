package com.time_em.barcode;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import java.util.ArrayList;
import java.util.HashMap;


public class BarcodeScanActivity extends Activity implements AsyncResponseTimeEm {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

  //  private int i = 0;
  //  private Button scanButton;
  // private String strUserIds="";

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private boolean refresh;
    private long scanCode=0;

    public static ArrayList<String> arrayList_scanCode = new ArrayList<>();
    private ArrayList<User> arrayListUsers = new ArrayList<>();
    private ArrayList<User> refreshArrayListUsers = new ArrayList<>();

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

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        // initControls();
        initUI();
        //setAdapter();
        // initControls();
        setOnClickListener();
        if (getIntent().getStringExtra("data") != null)
        {
             fetchUserByBarCode();
             setAdapter(arrayListUsers);
            }
    }


    private void initUI() {

        headerText=(TextView)findViewById(R.id.headerText);
        headerText.setText("Scanned Codes");
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
     /* scanButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    lay_listView.setVisibility(View.GONE);
                    preview.setVisibility(View.VISIBLE);

                   againOpenCamera();
                }
            });*/
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
                //releaseCamera();
                Utils.ChangeStatus(BarcodeScanActivity.this, ""+Ids,"signIn");

            }else if (v == btn_signOut) {

                String Ids=getAllUsersIds(arrayListUsers);
                Log.e("Ids=", Ids);
                //releaseCamera();
                Utils.ChangeStatus(BarcodeScanActivity.this, ""+Ids,"SignOut");
            }

        }
    };
  /*  private void initControls() {
        arrayList_scanCode.clear();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarcodeScanActivity.this, mCamera, previewCb, autoFocusCB);
        preview.addView(mPreview);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }
   *//**
     * A safe way to get an instance of the Camera object.
     *//*
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {


                    String scanResult = sym.getData().trim();
                    Log.i("<<<<<<Asset Code>>>>> ", "<<<<Bar Code>>> " + scanResult);
                if(arrayList_scanCode!=null && arrayList_scanCode.size()>0 ) {
                    boolean bolResult=true;
                   for(int i=0;i<arrayList_scanCode.size();i++){
                       if(arrayList_scanCode.get(i).equalsIgnoreCase(scanResult))
                       {
                           bolResult=false;
                        //   againOpenCamera();
                        //   Toast.makeText(BarcodeScanActivity.this, scanResult +" code already scanned",Toast.LENGTH_SHORT).show();
                            }
                        }
                   if(bolResult)
                   {
                       showAlertDialog(scanResult,bolResult);
                        }
                   else{
                       showAlertDialog(scanResult,bolResult);
                   }
               }
               else{
                   showAlertDialog(scanResult,true);
               }

                  *//*  Toast.makeText(BarcodeScanActivity.this, scanResult,
                            Toast.LENGTH_SHORT).show();*//*
                    barcodeScanned = true;
                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };


    private void showAlertDialog(String code,boolean result) {

        String massage="";
        //add value to array List
        // i++;
        // ScanDetail scan = new ScanDetail();
        // scan.setId(i);
        // scan.setCode(message);
        if (result){
            arrayList_scanCode.add(code);
            massage="Barcode scanned successfully. Do you want to scan another barcode?";
            }
        else{
            massage="This Barcode already scanned. Do you want to scan another barcode?";
        }

        new AlertDialog.Builder(this)
                .setTitle(code)
                .setCancelable(false)
                .setMessage(massage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        againOpenCamera();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fetchUserByBarCode();
                        setAdapter(arrayListUsers);
                        lay_listView.setVisibility(View.VISIBLE);
                        preview.setVisibility(View.GONE);
                        releaseCamera();
                    }
                })

                .show();
    }
    private void againOpenCamera()
    {
        if (barcodeScanned) {
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
            }
        }*/
    private void setAdapter(ArrayList<User> arrayList) {

        if (arrayList.size() == 0) {
            lay_listView.setVisibility(View.GONE);
        } else {
            adapter = new ListAdapter(arrayList, getApplicationContext());
            listView.setAdapter(adapter);
            lay_listView.setVisibility(View.VISIBLE);
        }

    }
    private void fetchUserByBarCode() {
        dbHandler = new TimeEmDbHandler(getApplicationContext());

        if(arrayList_scanCode!=null && arrayList_scanCode.size()>0) {
            for (int i = 0; i < arrayList_scanCode.size(); i++) {
                try {
                    scanCode = Long.parseLong(arrayList_scanCode.get(i).trim());
                    user = dbHandler.getTeamByLoginCode(scanCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Try again..", Toast.LENGTH_LONG).show();
                }

                if (user == null) {
                    refresh = false;
                    Toast.makeText(getApplicationContext(), "User not found " + scanCode, Toast.LENGTH_LONG).show();
                    getUserDetails(scanCode);
                } else {
                    arrayListUsers.add(user);
                }
            }
        }
       /* user = dbHandler.getTeamByLoginId(1001);
        arrayListUsers.add(user);
        user = dbHandler.getTeamByLoginId(1006);
        arrayListUsers.add(user);*/

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

        if (Utils.isNetworkAvailable(BarcodeScanActivity.this)) {
        //http://timeemapi.azurewebsites.net/api/User/GetUsersListByLoginCode?Logincode=9105
            HashMap<String, String> postDataParameters = new HashMap<String, String>();
            postDataParameters.put("Logincode", String.valueOf(userCode));

            AsyncTaskTimeEm mWebPageTask = new AsyncTaskTimeEm(
                    BarcodeScanActivity.this, "get", Utils.GetUsersListByLoginCode,
                    postDataParameters, true, "Please wait...");
            mWebPageTask.delegate = (AsyncResponseTimeEm) BarcodeScanActivity.this;
            mWebPageTask.execute();

        } else {
            Utils.alertMessage(BarcodeScanActivity.this, Utils.network_error);
        }
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
                e.printStackTrace(); }
            if(refresh)
            {
                arrayListUsers.addAll(teamMembers);
               // setAdapter(refreshArrayListUsers);
                setAdapter(arrayListUsers);
                Intent mIntent=new Intent(BarcodeScanActivity.this,CameraOpenActivity.class);
                startActivity(mIntent);
               // lay_listView.setVisibility(View.VISIBLE);
              //  preview.setVisibility(View.GONE);
              //  releaseCamera();
               // initControls();
               // againOpenCamera();

            }else {
                //arrayListUsers.clear();
                arrayListUsers.addAll(teamMembers);
               // refreshArrayListUsers.clear();
              //  setAdapter(arrayListUsers);
                setAdapter(arrayListUsers);
            }
       //     arrayListUsers.clear();
          //  arrayListUsers.addAll(refreshArrayListUsers);
            //refreshArrayListUsers.clear();


            //  TimeEmDbHandler dbHandler = new TimeEmDbHandler(BarcodeScanActivity.this);
            //  dbHandler.updateTeam(teamMembers);
            }
         else if(methodName.contains(Utils.SignInByUserId)){
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
                                 scanCode = Long.parseLong(arrayListUsers.get(i).getLoginCode());
                                 user = dbHandler.getTeamByLoginCode(scanCode);
                             } catch (Exception e) {
                                 e.printStackTrace();
                                 Toast.makeText(getApplicationContext(), "Try again..", Toast.LENGTH_LONG).show();
                             }

                             if (user == null) {
                                 refresh = true;
                                 //   Toast.makeText(getApplicationContext(),"User not found "+scanCode,Toast.LENGTH_LONG).show();
                                 getUserDetails(scanCode);
                             } else {
                                 refreshArrayListUsers.add(user);

                             }

                         }

                         arrayListUsers.clear();
                         arrayListUsers.addAll(refreshArrayListUsers);
                         refreshArrayListUsers.clear();
                         setAdapter(arrayListUsers);
                     }
                  else {
                         // Utils.alertMessage(BarcodeScanActivity.this, " User already Signed In.");
                     }
              }
          else if(methodName.contains(Utils.SignOutByUserId)){
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
                        scanCode = Long.parseLong(arrayListUsers.get(i).getLoginCode());
                        user=  dbHandler.getTeamByLoginCode(scanCode);
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Try again..",Toast.LENGTH_LONG).show();
                    }

                    if(user==null) {
                      //  Toast.makeText(getApplicationContext(),"User not found "+scanCode,Toast.LENGTH_LONG).show();
                        refresh=true;
                        getUserDetails(scanCode);

                    }
                    else {
                        refreshArrayListUsers.add(user);

                    }

                }
                arrayListUsers.clear();
                arrayListUsers.addAll(refreshArrayListUsers);
                refreshArrayListUsers.clear();
                setAdapter(arrayListUsers);

            }
                  else{
                     // Utils.alertMessage(BarcodeScanActivity.this, " User already Signed Out.");

                    }

                }

        }

}

