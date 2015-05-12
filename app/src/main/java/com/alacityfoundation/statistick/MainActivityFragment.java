package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * A placeholder fragment containing a simple view.
 */
@SuppressWarnings("unchecked")
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
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = 2011; i <= year ; i++) {
            add(i);
        }
    }};

    private Force selectedForce;
    private String selectedMonth;
    private String selectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = (Button) view.findViewById(R.id.goButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCrime(view);
            }
        });

        // perform web request to get list of all police forces
        new RequestTask().execute("https://data.police.uk/api/forces");

        // return inflated view
        return view;
    }

    // called on button press
    public void getCrime(View view) {
        Intent intent = new Intent(getActivity(), listActivity.class);
        String forceId = this.selectedForce.getId();
        String date = this.selectedYear + "-" + this.selectedMonth;
        intent.putExtra("requestUrl", "https://data.police.uk/api/crimes-no-location?category=all-crime&force=" + forceId + "&date=" + date);
        intent.putExtra("forceName", selectedForce.getName());
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
                Log.d("FORCE", selectedForce.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        // populate month & year spinners
        Spinner yearSpinner = (Spinner) this.getActivity().findViewById(R.id.yearSpinner);
        Spinner monthSpinner = (Spinner) this.getActivity().findViewById(R.id.monthSpinner);

        final ArrayAdapter<String> yearAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.years);
        final ArrayAdapter<String> monthAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.months);

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                SimpleDateFormat formatIn = new SimpleDateFormat("MMMM", Locale.UK);
                SimpleDateFormat formatOut = new SimpleDateFormat("MM", Locale.UK);
                try {
                    selectedMonth = formatOut.format(formatIn.parse(monthAdapter.getItem(position)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("MONTH", selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedYear = String.valueOf(yearAdapter.getItem(position));
                Log.d("YEAR", selectedYear);
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
