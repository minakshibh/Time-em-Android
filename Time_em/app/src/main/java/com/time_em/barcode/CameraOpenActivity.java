package com.time_em.barcode;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.time_em.android.R;
import com.time_em.utils.Utils;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;



public class CameraOpenActivity extends Activity {

    //todo classes
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    //todo widgets
    private ImageScanner scanner;
    private FrameLayout preview;
    private TextView headerText;
    private ImageView back, AddButton;

    //todo variables
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    static {
        System.loadLibrary("iconv");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraopen);


        initUI();
        initControls();
        setOnClickListener();
    }



    private void initUI() {

        headerText=(TextView)findViewById(R.id.headerText);
        headerText.setText("Barcode Scanning");
        back=(ImageView)findViewById(R.id.back);
        AddButton=(ImageView)findViewById(R.id.AddButton);
        AddButton.setVisibility(View.GONE);
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.setVisibility(View.VISIBLE);

        if(getIntent().getStringExtra("data")!=null)
        {
            BarcodeScanActivity.arrayList_scanCode.clear();
            }

    }

    private void setOnClickListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseCamera();
                Intent mIntent=new Intent(CameraOpenActivity.this,BarcodeScanActivity.class);
                mIntent.putExtra("trigger","barcode");
                startActivity(mIntent);
                finish();;
            }
        });
    }

    private void initControls() {

        boolean result= Utils.checkPermission(getApplicationContext());
        if(result) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            autoFocusHandler = new Handler();
            mCamera = getCameraInstance();
            // Instance barcode scanner
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            mPreview = new CameraPreview(CameraOpenActivity.this, mCamera, previewCb, autoFocusCB);
            preview.addView(mPreview);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * A safe way to get an instance of the Camera object.
     */
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
                    if(BarcodeScanActivity.arrayList_scanCode!=null && BarcodeScanActivity.arrayList_scanCode.size()>0 ) {
                        boolean bolResult=true;
                        for(int i=0;i<BarcodeScanActivity.arrayList_scanCode.size();i++){
                            if(BarcodeScanActivity.arrayList_scanCode.get(i).equalsIgnoreCase(scanResult))
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

                  /*  Toast.makeText(BarcodeScanActivity.this, scanResult,
                          Toast.LENGTH_SHORT).show();*/
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


    private void showAlertDialog(final String code, boolean result) {

        String massage="";

        if (result){
            BarcodeScanActivity.arrayList_scanCode.add(code);
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
                       // fetchUserByBarCode();
                       // setAdapter(arrayListUsers);
                      //  lay_listView.setVisibility(View.VISIBLE);
                      //  preview.setVisibility(View.GONE);
                        releaseCamera();
                        Intent mIntent=new Intent(CameraOpenActivity.this,BarcodeScanActivity.class);
                        mIntent.putExtra("trigger","barcode");
                        startActivity(mIntent);
                        finish();
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
    }
    public void onPause() {
        super.onPause();
        releaseCamera();
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.removeView(mPreview);
    }

    public void onResume(){
        super.onResume();

        try {
            if(mCamera==null){

                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                autoFocusHandler = new Handler();
                mCamera = getCameraInstance();
                this.getWindowManager().getDefaultDisplay().getRotation();

                scanner = new ImageScanner();
                scanner.setConfig(0, Config.X_DENSITY, 3);
                scanner.setConfig(0, Config.Y_DENSITY, 3);

                mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
                FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
                preview.addView(mPreview);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block

        }
    }
}
