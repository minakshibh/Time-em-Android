package com.time_em.barcode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.time_em.android.R;
import com.time_em.utils.Utils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NFCReadActivity extends Activity  {

    private static final String MIME_TYPE="vnd.secret/agent.man";
    private static final Charset US_ASCII=Charset.forName("US-ASCII");
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private NfcAdapter mNfcAdapter=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcread);



        if(getIntent().getStringExtra("data")!=null)
        {
            BarcodeScanActivity.arrayList_scanCode.clear();
            }

        getNFCCode();

    }

    private void getNFCCode() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (!mNfcAdapter.isEnabled()) {
            // Stop here,if nfc disable.
            Toast.makeText(getApplicationContext(), "NFC is disabled. Please set enable and try again", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
Log.e("on resume","resume called");
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        Log.e("Intent recieved","nfc discovered");
        handleIntent(intent);
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        /*IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }*/

        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }


    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void handleIntent(Intent intent) {
        Log.e("handle intent","intent called");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                tag.getId();
                Utils.alertMessage(NFCReadActivity.this,String.valueOf(tag.getId()));
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
                Utils.alertMessage(getApplicationContext(),"line 156"+type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Utils.alertMessage(NFCReadActivity.this,String.valueOf(tag.getId()));
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }else{
            Utils.alertMessage(NFCReadActivity.this,"Code in else clause, action: "+action);
        }
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                        Utils.alertMessage(getApplicationContext(),"line 200");
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String scanResult) {
            if (scanResult != null) {
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
                        showAlertDialog(scanResult, bolResult);
                    }
                    else{
                        showAlertDialog(scanResult,bolResult);
                    }
                }
                else{
                    showAlertDialog(scanResult,true);
                }
            }
            else{
                Utils.alertMessage(getApplicationContext(),"261 on post");
            }
        }
    }

    private void showAlertDialog(final String code, boolean result) {

        String massage="";

        if (result){
            BarcodeScanActivity.arrayList_scanCode.add(code);
            massage="NFC read content successfully.";
        }
        else{
            massage="This NFC content already read.";
        }

        new AlertDialog.Builder(this)
                .setTitle(code)
                .setCancelable(false)
                .setMessage(massage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mIntent=new Intent(NFCReadActivity.this,BarcodeScanActivity.class);
                        mIntent.putExtra("trigger","nfc");
                        startActivity(mIntent);
                        finish();

                    }
                })
               .show();
    }
}