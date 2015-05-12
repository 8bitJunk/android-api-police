package com.alacityfoundation.statistick;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by ryan on 12/05/2015.
 */
public class SpinAdapter extends ArrayAdapter<Object> {
    private Context context;
    private Object[] values;

    public SpinAdapter(Context context, int textViewResourceId, Object[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount() {
        return values.length;
    }

    public Object getItem(int position) {
        return values[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        label.setText(((Force) values[position]).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        label.setText(((Force) values[position]).getName());

        return label;
    }
}