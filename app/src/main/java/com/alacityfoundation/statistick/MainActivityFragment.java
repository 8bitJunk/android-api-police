package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskCompleted {
    private ArrayList<Force> forces = new ArrayList<>();
    private ArrayList<Crime> crimes = new ArrayList<>();
    private final ArrayList<String> months = new ArrayList<String>() {{
        add("January");
        add("February");
        add("March");
        add("April");
        add("May");
        add("June");
        add("July");
        add("August");
        add("September");
        add("October");
        add("November");
        add("December");
    }};
    private final ArrayList<Integer> years = new ArrayList<Integer>() {{
        for(int i = 2000; i < 2016; i++) {
            add(i);
        }
    }};

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // perform web request to get list of all police forces
        new RequestTask(this, new Force()).execute("https://data.police.uk/api/forces");

        // loop through results and add to spinner
        Spinner forceSpinner = (Spinner) this.getActivity().findViewById(R.id.forceSpinner);
        ArrayAdapter<String> forceAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.forces);
        forceSpinner.setAdapter(forceAdapter);

        // populate month & year spinners
        Spinner yearSpinner = (Spinner) this.getActivity().findViewById(R.id.yearSpinner);
        Spinner monthSpinner = (Spinner) this.getActivity().findViewById(R.id.monthSpinner);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.years);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.months);

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);

        // return inflated view
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * Processes the results from the AsyncTask
     *
     * @param obj The class we expect the results to be instances of, used to deserialise them
     * @param result The JSON encoded data retrieved from the HTTP Request
     */
    @Override
    public void onTaskCompleted(Object obj, String result) {
        Log.d("RESULTS", result);
        try {
            // deserialise the json objects and add to the appropriate instance variable
            JSONArray jsonResult = new JSONArray(result);
            for(int i = 0; i < jsonResult.length(); i++) {
                Object resultObject = this.decodeJson(obj.getClass(), jsonResult.getString(i));
                if(obj instanceof Force) {
                    this.forces.add((Force) resultObject);
                } else if (obj instanceof Crime) {
                    this.crimes.add((Crime) resultObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    // called on button press
    public void getCrime(View view) {

    }

    // turns JSON into clazz object
    private <T> T decodeJson(Class<T> clazz, String json) {
        Object obj = new Gson().fromJson(json, clazz);
        return (T) obj;
    }
}
