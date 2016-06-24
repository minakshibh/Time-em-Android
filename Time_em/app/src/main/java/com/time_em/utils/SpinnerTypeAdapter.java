package com.time_em.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.RequiresPermission;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.time_em.android.R;
import com.time_em.model.SpinnerData;

import java.util.ArrayList;

/**
 * Created by minakshi on 08/06/16.
 */
public class SpinnerTypeAdapter extends ArrayAdapter<SpinnerData> {

    // Your sent context
    private ArrayList<SpinnerData> spinnerList;
    private Context context;
    // Your custom values for the spinner (User)

    public SpinnerTypeAdapter(Context context, int textViewResourceId, ArrayList<SpinnerData> spinnerList) {
        super(context, textViewResourceId);
        this.context = context;
        this.spinnerList = spinnerList;
    }

    public int getCount() {
        return spinnerList.size();
    }

    public SpinnerData getItem(int position) {
        return spinnerList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(spinnerList.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,  ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mySpinner = inflater.inflate(R.layout.spinner_dropdown, parent, false);

        TextView label = (TextView)mySpinner.findViewById(R.id.text);
        label.setTextColor(Color.BLACK);
        label.setText(spinnerList.get(position).getName());

        return label;
    }
}