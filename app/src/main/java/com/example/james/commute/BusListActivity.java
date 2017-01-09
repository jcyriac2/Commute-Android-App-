package com.example.james.commute;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusListActivity extends AppCompatActivity {
    String stopid;
    private RequestQueue networkqueue;
    private List<BusDetails> busDetails;
    private ListView BusListArray;
    private Context context;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        context=this;
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("BusStopName"));
        stopid = intent.getStringExtra("BusStopID");
        busDetails = new ArrayList<BusDetails>();
        networkqueue = Volley.newRequestQueue(this);
        BusListArray = (ListView) findViewById(R.id.bus_list_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DisplayBuses();
            }
        });
        DisplayBuses();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DisplayBuses(){
        StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.getBusTimeAPI) + "&stop_id=" + stopid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            busDetails.clear();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("departures");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject buses = jsonArray.getJSONObject(i);

                                BusDetails temp = new BusDetails();
                                temp.setNumber(buses.getString("headsign"));
                                temp.setETA(buses.getInt("expected_mins"));
                                temp.setColor(buses.getJSONObject("route").getString("route_color"));

                                busDetails.add(temp);

                            }
                            BusListArray.setAdapter(new BusETAListAdapter(context,busDetails));
                            swipeContainer.setRefreshing(false);
                        } catch (Exception e) {
                            Log.d("NETWORK", "JSON RETRIEVAL FAILED");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        networkqueue.add(request);

    }
}
