package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayList<Force> forces = new ArrayList<>();
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
    private Force selectedForce = null;
    private String selectedMonth = null;
    private String selectedYear = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // perform web request to get list of all police forces
        new RequestTask().execute("https://data.police.uk/api/forces");

        // return inflated view
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // called on button press
    public void getCrime(View view) {
        Intent intent = new Intent(getActivity(), listActivity.class);
        String forceId = this.selectedForce.getId();
        String date = this.selectedYear + "-" + this.selectedMonth;
        intent.putExtra("requestUrl", "https://data.police.uk/api/crimes-no-location?category=all-crime&force=" + forceId + "&date=" + date);
        startActivity(intent);
    }

    private void populateSpinners() {
        // loop through results and add to spinner
        final SpinAdapter adapter = new SpinAdapter(this.getActivity(), android.R.layout.simple_spinner_item, forces.toArray());
        Spinner forceSpinner = (Spinner) this.getActivity().findViewById(R.id.forceSpinner);
        forceSpinner.setAdapter(adapter); // Set the custom adapter to the spinner
        // Handle the event when an item in the spinner is clicked
        forceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // set the selectedForce to be the force chosen in the spinner
                selectedForce = (Force) adapter.getItem(position);
                Log.d("SELECTED FORCE", selectedForce.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        // populate month & year spinners
        Spinner yearSpinner = (Spinner) this.getActivity().findViewById(R.id.yearSpinner);
        Spinner monthSpinner = (Spinner) this.getActivity().findViewById(R.id.monthSpinner);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.years);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.months);

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // set the selectedForce to be the force chosen in the spinner
                Log.d("SELECTED MONTH", (String) adapter.getItem(position));
                String monthValue;
                switch ((String) adapter.getItem(position)) {
                    case "January":
                        monthValue = "01";
                        break;
                    case "February":
                        monthValue = "02";
                        break;
                    case "March":
                        monthValue = "03";
                        break;
                    case "April":
                        monthValue = "04";
                        break;
                    case "May":
                        monthValue = "05";
                        break;
                    case "June":
                        monthValue = "06";
                        break;
                    case "July":
                        monthValue = "07";
                        break;
                    case "August":
                        monthValue = "08";
                        break;
                    case "September":
                        monthValue = "09";
                        break;
                    case "October":
                        monthValue = "10";
                        break;
                    case "November":
                        monthValue = "11";
                        break;
                    case "December":
                        monthValue = "12";
                        break;
                    default:
                        monthValue = "01";
                        break;
                }
                selectedMonth = monthValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedYear = (String) adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // inner class for dealing with the getting and deserialising of crime and force objects from API
    public class RequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            String result = "";
            try {
                result = new UrlDownloader().downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.d("OH DEAR", "Something went wrong");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("RESULTS", result);
            try {
                // deserialise the json objects and add to the appropriate instance variable
                JSONArray jsonResult = new JSONArray(result);
                for(int i = 0; i < jsonResult.length(); i++) {
                    // for all results in array, deserialise into returnObjectType objects
                    Force resultObject = this.decodeJson(Force.class, jsonResult.getString(i));
                    forces.add(resultObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // populate spinners with new ly deserialised objects
            populateSpinners();
        }

        // turns JSON into clazz object
        private <T> T decodeJson(Class<T> clazz, String json) {
            Object obj = new Gson().fromJson(json, clazz);
            return (T) obj;
        }
    }
}
