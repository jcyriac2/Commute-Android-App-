package com.example.james.commute;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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

/**
 * Created by james on 12/23/2016.
 */

public class ETAListFragment extends Fragment {

    String stopid;
    private RequestQueue networkqueue;
    private List<BusDetails> busDetails;
    private List<BusDetails> filteredBusDetails;
    private ListView BusListArray;
    private ListView DestSearchList;
    private Context context;
    private SwipeRefreshLayout swipeContainer;
    private Bundle bundle;
    private List<BusStopDetails> autocompletestops;
    private List<String> RouteNumber;

    public ETAListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        networkqueue = Volley.newRequestQueue(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,Bundle savedInstanceState){
        View newView= inflator.inflate(R.layout.activity_bus_list,container,false);

        bundle = new Bundle();
        bundle =this.getArguments();
        if(bundle!=null){
            stopid = bundle.getString("stop",stopid);
        }
        swipeContainer = (SwipeRefreshLayout) newView.findViewById(R.id.swipeContainer);
        BusListArray = (ListView) newView.findViewById(R.id.bus_list_display);
        DestSearchList = (ListView) newView.findViewById(R.id.DestSearchList);
        busDetails = new ArrayList<BusDetails>();
        autocompletestops = new ArrayList<BusStopDetails>();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DisplayBuses();
            }
        });
        DisplayBuses();

        //adding search functionality

        final EditText DestSearchText = (EditText) newView.findViewById(R.id.destSearchEdt);

        DestSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Change List visibility
                if(s.length()==0){
                    DestSearchList.setVisibility(View.INVISIBLE);
                    BusListArray.setVisibility(View.VISIBLE);
                }
                else {
                    DestSearchList.setVisibility(View.VISIBLE);
                    BusListArray.setVisibility(View.INVISIBLE);

                    //Do auto-complete

                    updateDestList(DestSearchText.getText().toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });

        DestSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusStopDetails clickedStop = autocompletestops.get(position);
                String destStopid = clickedStop.getID();

                //get RouteNumbers between 2 stops
                RouteNumber = getRouteNumbers(stopid,destStopid);

                if(RouteNumber.size()==0){
                    //DisplayNoBuses();
                }

                else{
                    filteredBusDetails = new ArrayList<BusDetails>();
                    for(int i=0;i<busDetails.size();i++){
                        for(int j=0;j<RouteNumber.size();j++){
                            if(busDetails.get(i).getRouteNumber()==RouteNumber.get(j)){
                                filteredBusDetails.add(busDetails.get(i));
                            }
                        }
                    }

                    BusListArray.setAdapter(new BusETAListAdapter(getActivity(),filteredBusDetails));
                    Log.d("TAG","Filtered Bus List");
                    DestSearchList.setVisibility(View.INVISIBLE);
                    BusListArray.setVisibility(View.VISIBLE);
                }




            }
        });

        return newView;
    }

    public List<String> getRouteNumbers(String startid,String stopid){
        final List<String> routeNumbers = new ArrayList<>();

        StringRequest request3 = new StringRequest(Request.Method.GET, getString(R.string.getPlannedTripsByStops) + "&origin_stop_id=" + startid + "&destination_stop_id=" + stopid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("itineraries");
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObjectItinerary1 = jsonArray.getJSONObject(j);
                                JSONArray jsonArraylegs = jsonObjectItinerary1.getJSONArray("legs");
                                for (int i = 0; i < jsonArraylegs.length(); i++) {
                                    JSONObject jsonObjectLegType = jsonArraylegs.getJSONObject(i);
                                    if (jsonObjectLegType.getString("type") != "Service")
                                        continue;

                                    JSONArray jsonArrayServices = jsonObjectLegType.getJSONArray("services");
                                    if (jsonArrayServices.length() == 1) {
                                        JSONObject jsonObjectService1 = jsonArrayServices.getJSONObject(0);
                                        JSONObject jsonObjectRouteDetails = jsonObjectService1.getJSONObject("route");
                                        routeNumbers.add(jsonObjectRouteDetails.getString("route_short_name"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d("TAG","FAILED GETTING ROUTE NUMBERS");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do nothing
            }
        });
        return routeNumbers;
    }

    public void DisplayBuses(){
        Log.d("TAG",stopid+" or no data");
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
                                temp.setRouteNumber(buses.getJSONObject("route").getString("route_short_name"));

                                busDetails.add(temp);

                            }
                            BusListArray.setAdapter(new BusETAListAdapter(getActivity(),busDetails));
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

    void updateDestList(String ustring){
        StringRequest request2 = new StringRequest(Request.Method.GET, getString(R.string.autoCompleteAPI) + ustring, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List stopResults = new ArrayList();
                try {
                    autocompletestops.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String Stopname = jsonArray.getJSONObject(i).getString("n");
                        Log.d("TAG", Stopname);
                        stopResults.add(Stopname);
                        final BusStopDetails stop = new BusStopDetails();
                        stop.setName(jsonArray.getJSONObject(i).getString("n"));
                        stop.setID(jsonArray.getJSONObject(i).getString("i"));
                        autocompletestops.add(stop);
                    }

                    DestSearchList.setAdapter(new StopListAdapter(getActivity(), stopResults));
                } catch (Exception e) {
                    Log.d("TAG", "NETWORK ERROR");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do nothing
            }
        });

        networkqueue.add(request2);
    }
}
