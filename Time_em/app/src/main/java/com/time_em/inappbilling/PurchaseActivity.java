package com.time_em.inappbilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.inappbilling.util.IabHelper;
import com.time_em.inappbilling.util.IabResult;
import com.time_em.inappbilling.util.Inventory;
import com.time_em.inappbilling.util.Purchase;
import com.time_em.notifications.SendNotification;

public class PurchaseActivity extends Activity {

    private static final String TAG = "InAppBilling";
    IabHelper mHelper;
    private Button clickButton;
    private Button buyButton;
    TextView buyButton2,cancel;
    //static final String ITEM_SKU = "truesecrets.inapp.ts1800";
    static final String ITEM_SKU = "android.test.purchased";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);
        buyButton2= (TextView) findViewById(R.id.buyButton2);
        cancel=(TextView) findViewById(R.id.buyButton1);
        clickButton.setEnabled(false);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyWaOjWjjsXoBiQfREc0BwLx844Wgg0Pm5ExCgEqsVuedBNPJlwomcag4v+/sjEfN9NuF0HMCvrA6GtbD55YgU8tQs2QdEIkkM6UkAFhH1V4g7kp61sDZI8MQcqutY8H/vYqjm13GxiDZOKIHEKwEffglOYlJh9enHc2Hs3HusipkuPHN215Beftt4WZuR2iPCb7kN8uuCYUaWL4a40Fa2OdUSPi+0efUyYfQDjuoQobtxxcBf9MgvgxiGiI91GPW2d8v79bnN6ID3BLE5Ia01f9SUiLyxsxrgLdcDqdvzdgEiXM9YZt0MffQPRnCpPh3VMlQ05xyJzGh87KXG9DIGwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss(v);
            }
        });

        buyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss(v);
                buyClick(v);
            }
        });

    }

    public void buttonClicked (View view)
    {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }
    public void dissmiss (View view)
    {
        this.finish();
    }

    public void buyClick(View view) {
        try {
            Log.d(TAG, "*****buyClick*****");
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
           if(result.getResponse()==IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED)
            {
                Log.d(TAG, "*****7*****"+purchase);
                Intent in=new Intent(getApplicationContext(), SendNotification.class);
                startActivity(in);
            }
           else if (result.isFailure()) {
                // Handle error
                Log.d(TAG, "*****Failure purchase*****"+purchase);
                return;
            }
           else if (purchase.getSku().equals(ITEM_SKU)) {
                Intent in=new Intent(getApplicationContext(), SendNotification.class);
                startActivity(in);
                consumeItem();
                cancel.setEnabled(false);
                buyButton2.setEnabled(false);
            }else {
                Log.d(TAG, "*****else failedpurchase*****"+purchase);
            }
        }
    };

    public void consumeItem()  {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    if (result.isSuccess()) {
                        finish();
                        //clickButton.setEnabled(true);

                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            try {

                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        mHelper = null;
    }


}
