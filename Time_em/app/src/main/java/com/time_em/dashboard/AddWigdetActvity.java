package com.time_em.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.time_em.android.R;

import java.util.ArrayList;


public class AddWigdetActvity extends Activity {

    private GridView gridView;

    private SelectViewAdapter selectViewAdapter;

    public ArrayList<String> backGroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wigdet);
        backGroundColor = new ArrayList<>();

        backGroundColor.add("#63C070");
        backGroundColor.add("#B83F3A");
        backGroundColor.add("#51B3CE");
        backGroundColor.add("#63C070");
        backGroundColor.add("#B83F3A");
        backGroundColor.add("#51B3CE");
        backGroundColor.add("#63C070");
        backGroundColor.add("#B83F3A");
        backGroundColor.add("#51B3CE");

        inItScreen();
    }

    private void inItScreen() {
        gridView = (GridView) findViewById(R.id.addView_GrideView);
        selectViewAdapter = new SelectViewAdapter(this, backGroundColor);
        gridView.setAdapter(selectViewAdapter);
    }

    public class SelectViewAdapter extends BaseAdapter {

        ArrayList<String> backGroundColor;
        Context context;
        int[] imageId;
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

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder = new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.template_add_widget_view, null);
            holder.SingleGrideView = (RelativeLayout) rowView.findViewById(R.id.SingleGrideView);
            holder.SingleGrideView.setBackgroundColor(Color.parseColor(backGroundColor.get(position)));

            return rowView;
        }

    }
}
