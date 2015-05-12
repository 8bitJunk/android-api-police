package com.alacityfoundation.statistick;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ryan on 12/05/2015.
 */
public class listAdapter extends ArrayAdapter<Object> {
    private Context context;
    private Object[] values;

    public listAdapter(Context context, int textViewResourceId, Object[] values) {
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
        View itemView = convertView;

        // ensure we have a view to work with.
        if(null == itemView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.crime_list, parent, false);
        }

        // find the note to work with
        Crime crime = (Crime) values[position];

        // fill the view
        ImageView image = (ImageView) itemView.findViewById(R.id.imageView);
        TextView category = (TextView) itemView.findViewById(R.id.category);
        TextView outcome = (TextView) itemView.findViewById(R.id.outcome);
        TextView location = (TextView) itemView.findViewById(R.id.location);
        TextView month = (TextView) itemView.findViewById(R.id.month);

        image.setVisibility(View.GONE);
        category.setText(crime.getCategory());
        outcome.setText("Outcome: " + crime.getOutcome_status());
        location.setText("Location: " + crime.getLocation());
        month.setText(crime.getMonth());

        return itemView;
    }
}