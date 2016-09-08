package com.time_em.dashboard;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.time_em.android.R;
import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;


public class SettingActivity extends Activity{
    //todo widgets
    private TextView headerInfo,txtHeader,txtBuildVersion,txtBuildVersionOutput,txtBuildSizeOutput;
    private ImageView back,AddButton;
    private Semaphore codeSizeSemaphore = new Semaphore(1, true);


public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);



        initUIComponents();
        setValue();
        sliderOnClickListener();

        }

private void setValue() {
        // TODO Auto-generated method stub
        PackageManager manager = this.getPackageManager();
        PackageInfo info;
        try {
        info = manager.getPackageInfo(this.getPackageName(), 0);
        txtBuildVersionOutput.setText( ""+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }

        txtBuildSizeOutput.setText(getFileSize(getSize())+"");
        }

private void initUIComponents() {
        // TODO Auto-generated method stub

        headerInfo = (TextView)findViewById(R.id.headerText);
        headerInfo.setText("Settings");
        back =(ImageView)findViewById(R.id.back);
        AddButton=(ImageView)findViewById(R.id.AddButton);
        AddButton.setVisibility(View.GONE);

        txtBuildVersion=(TextView)findViewById(R.id.txtBuildVersion);
        txtBuildVersionOutput=(TextView)findViewById(R.id.txtBuildVersionOutput);
        //txtBuildSize=(TextView)findViewById(R.id.txtBuildSize);
        txtBuildSizeOutput=(TextView)findViewById(R.id.txtBuildSizeOutput);

        }
private View.OnClickListener listener = new View.OnClickListener() {

@Override
public void onClick(View v) {
       if (v == back) {

        finish();
        }

        }
        };

    public void sliderOnClickListener() {

            back.setOnClickListener(listener);
       }
public long getSize() {
        long size=0;
        ApplicationInfo tmpInfo;
        try {
        tmpInfo = SettingActivity.this.getPackageManager().getApplicationInfo(getPackageName(),-1);
        File file = new File(tmpInfo.publicSourceDir);
        size = file.length();

        } catch (PackageManager.NameNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        return size;

        }
public static String getFileSize(long size) {
    if (size <= 0)
        return "0";
    final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
}     }