package com.time_em.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.time_em.android.R;
import com.time_em.model.User;
import com.time_em.parser.Time_emJsonParser;



public class GeoGraphsActivity extends Activity {


    private Time_emJsonParser parser;
    private User mUser;
    private LinearLayout mainLinearLayout,lay_date,lay_hours;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initScreen();
        settingGraph();
    }



    private void initScreen() {
        mainLinearLayout = (LinearLayout)findViewById(R.id.graphLayout);
        lay_date= (LinearLayout)findViewById(R.id.lay_date);
       // lay_hours= (LinearLayout)findViewById(R.id.lay_hours);
    }


        private void settingGraph() {

                for(int i=0;i<5;i++) {

                    // Create LinearLayout
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    //linearLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                    linearLayout.setPadding(0, 10, 10, 0);

                    // Add text view
                    for (int j = 0; j < 24; j++) {
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

