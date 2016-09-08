package com.time_em.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.model.Widget;
import com.time_em.utils.Utils;

import java.util.ArrayList;


public class AddWigdetActvity extends Activity {

    //todo widgets
    private GridView gridView;
    private TextView headerInfo,dateHeader;
    private ImageView back,AddButton;
    private SelectViewAdapter selectViewAdapter;
    private LinearLayout lay_GridView;
    //todo array list
    private ArrayList<String> backGroundColor;
    private ArrayList<String> labelName;
    private ArrayList<Widget> View_arrayList_widget = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wigdet);



        View_arrayList_widget.clear();
        View_arrayList_widget=Utils.getWidget(AddWigdetActvity.this);
        arrayValues();
        inItScreen();
        setOnClickListener();
    }



    private void arrayValues() {
        backGroundColor = new ArrayList<>();
        backGroundColor.add("#63C070");
        backGroundColor.add("#B83F3A");
        backGroundColor.add("#51B3CE");

        backGroundColor.add("#DA70D6");
        backGroundColor.add("#FF00FF");
        backGroundColor.add("#912CEE");

        backGroundColor.add("#FF33F0");
        backGroundColor.add("#FF55F0");
        backGroundColor.add("#FFFF00");



        labelName = new ArrayList<>();
        labelName.add("Label 1");
        labelName.add("Label 2");
        labelName.add("Label 3");
        labelName.add("Label 4");
        labelName.add("Label 5");
        labelName.add("Label 6");
        labelName.add("Label 7");
        labelName.add("Label 8");
        labelName.add("Label 9");
    }

    private void inItScreen() {
        lay_GridView=(LinearLayout)findViewById(R.id.lay_GridView);
        lay_GridView.setBackgroundColor(getResources().getColor(R.color.white));
        headerInfo = (TextView)findViewById(R.id.headerText);
        headerInfo.setText("Widgets");
        back =(ImageView)findViewById(R.id.back);
        AddButton=(ImageView)findViewById(R.id.AddButton);
        AddButton.setVisibility(View.GONE);
        dateHeader=(TextView)findViewById(R.id.dateHeader);
        dateHeader.setText("Done");
        dateHeader.setPadding(5,5,5,5);
        dateHeader.setVisibility(View.VISIBLE);
        gridView = (GridView) findViewById(R.id.addView_GrideView);
        selectViewAdapter = new SelectViewAdapter(this, backGroundColor);
        gridView.setAdapter(selectViewAdapter);

    }
    private void setOnClickListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        dateHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // HomeActivity.arrayList_widget.addAll(arrayList_widget);
                finish();
            }
        });
    }
    public class SelectViewAdapter extends BaseAdapter {

        ArrayList<String> backGroundColor;
        Context context;
        private LayoutInflater inflater = null;

        public SelectViewAdapter(Activity activity, ArrayList<String> backGroundColor) {
            // TODO Auto-generated constructor stub
            this.backGroundColor = backGroundColor;
            context = activity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return backGroundColor.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder {
            public RelativeLayout SingleGrideView;
            CheckBox checkBox;
            TextView textView_name;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.template_add_widget_view, null);
            holder.SingleGrideView = (RelativeLayout) rowView.findViewById(R.id.SingleGrideView);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            holder.textView_name = (TextView) rowView.findViewById(R.id.textView_name);

            holder.textView_name.setText(labelName.get(position));
            holder.SingleGrideView.setBackgroundColor(Color.parseColor(backGroundColor.get(position)));
            if (View_arrayList_widget != null && View_arrayList_widget.size() > 0) {
                for (int i = 0; i < View_arrayList_widget.size(); i++) {
                    if (View_arrayList_widget.get(i).getPosition() == position) {
                        holder.checkBox.setChecked(true);
                    }
                }
            }
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View_arrayList_widget=new ArrayList<Widget>();
                        View_arrayList_widget.clear();
                        View_arrayList_widget=Utils.getWidget(AddWigdetActvity.this);

                          if (holder.checkBox.isChecked()) {
                            System.out.println("Checked");
                            Widget widget = new Widget();
                            widget.setColor(backGroundColor.get(position));
                            widget.setName(labelName.get(position));
                            widget.setPosition(position);
                              boolean value=true;
                              if(View_arrayList_widget!=null) {
                                  for (int i = 0; i < View_arrayList_widget.size(); i++) {
                                      if (position == View_arrayList_widget.get(i).getPosition()) {
                                          value = false;
                                      }
                                  }
                              }
                              if(value) {
                                  Utils.addValues(AddWigdetActvity.this, widget);
                              }
                            //arrayList_widget.add(widget);
                        }
                          else {
                            System.out.println("Un-Checked");

                            if (View_arrayList_widget != null && View_arrayList_widget.size() > 0) {
                                //String name = holder.textView_name.getText().toString();
                                for (int i = 0; i < View_arrayList_widget.size(); i++) {
                                    if (position==View_arrayList_widget.get(i).getPosition()) {
                                        try {
                                            Utils.removeValues(AddWigdetActvity.this, i);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }

                        }
                    }
                });
                return rowView;
            }

        }
}
