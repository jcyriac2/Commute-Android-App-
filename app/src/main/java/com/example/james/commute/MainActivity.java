package com.example.james.commute;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

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


public class MainActivity extends AppCompatActivity {
    EditText edtDepStop;
    ListView ArrStopList;
    Button nearbybutton;
    private Context context;
    private LocCord currLocation;
    //location manager
    private LocationManager locationManager;
    //location listener
    private LocationListener locationListener;
    //list adapter
    private NearbyListAdapter nearbyListAdapter;
    //bus stop list
    private List<BusStopDetails> busStops;
    private List<BusStopDetails> autocompletestops;

    private RequestQueue networkqueue;

    private ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        networkqueue = Volley.newRequestQueue(this);
        edtDepStop = (EditText) findViewById(R.id.edtDepStop);
        ArrStopList = (ListView) findViewById(R.id.depstoplist);
        nearbybutton = (Button) findViewById(R.id.button_nearby);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        currLocation = new LocCord(0, 0);
        //nearby bus stop variables
        busStops = new ArrayList<BusStopDetails>();
        autocompletestops = new ArrayList<BusStopDetails>();
        nearbyListAdapter =new NearbyListAdapter(context,busStops);

        //to go to display list of buses in the BusListActivity
        ArrStopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusStopDetails clickedStop = autocompletestops.get(position);
                //intent to BusListActivity for normal list
                Intent intent = new Intent(context,BusListTabsActivity.class);
                intent.putExtra("BusStopID",clickedStop.getID());
                intent.putExtra("BusStopName",clickedStop.getName());
                intent.putExtra("BusStopLat",clickedStop.getLocation().getLat());
                intent.putExtra("BusStopLon",clickedStop.getLocation().getLon());
                intent.putExtra("CurrLat",currLocation.getLat());
                intent.putExtra("CurrLon",currLocation.getLon());
                startActivity(intent);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLocation.set(location.getLongitude(), location.getLatitude());
                Log.d("OnLocChanged", location.getLongitude() + " " + location.getLatitude());
                updateNearbyList();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //later
            }

            @Override
            public void onProviderEnabled(String provider) {
                //later
            }

            @Override
            public void onProviderDisabled(String provider) {
                //later
            }
        };

        //checking if location settings are turned on
        if (!isLocationEnabled())
            showLocSetAlert();

        getCurrLocation();

        nearbybutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                updateNearbyList();
                displayNearbyList();
            }
        });

        edtDepStop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateStartList(edtDepStop.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateStartList(String ustring) {
        StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.autoCompleteAPI) + ustring, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List stopresults = new ArrayList();
                try {
                    autocompletestops.clear();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String Stopname = jsonArray.getJSONObject(i).getString("n");
                        Log.d("TAG", Stopname);
                        stopresults.add(Stopname);
                        final BusStopDetails stop = new BusStopDetails();
                        stop.setName(jsonArray.getJSONObject(i).getString("n"));
                        stop.setID(jsonArray.getJSONObject(i).getString("i"));
                        ///To get Stop Location -------------------------------------------------------
                        StringRequest request2 = new StringRequest(Request.Method.GET, getString(R.string.getStopDetailsAPI) + "&stop_id=" + stop.getID(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            JSONArray jsonArray1 = jsonObject.getJSONArray("stops");
                                            JSONObject stop1 = jsonArray1.getJSONObject(0);
                                            JSONArray jsonArray2 = stop1.getJSONArray("stop_points");
                                            JSONObject details = jsonArray2.getJSONObject(0);
                                            LocCord loc = new LocCord(details.getDouble("stop_lon"),details.getDouble("stop_lat"));
                                            stop.setLocation(loc);

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

                        networkqueue.add(request2);
                        //////////------------------------------------------------

                        autocompletestops.add(stop);
                    }
                    ArrStopList.setAdapter(new StopListAdapter(context, stopresults));
                } catch (Exception e) {
                    stopresults.add("Network Error");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //do nothing
            }
        }
        );

        networkqueue.add(request);
    }

    public void updateNearbyList() {
        //suggest nearby stops

        Log.d("TAG", currLocation.getLat() + " " + currLocation.getLon());

        StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.getNearbyAPI) + "&lat=" + currLocation.getLat() + "&lon=" + currLocation.getLon(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonStopArray = jsonObject.getJSONArray("stops");
                            busStops.clear();
                            for(int i=0;i<jsonStopArray.length();i++){
                                JSONObject stopdetails = jsonStopArray.getJSONObject(i);

                                BusStopDetails stop = new BusStopDetails();
                                stop.setName(stopdetails.getString("stop_name"));
                                stop.setDistance(stopdetails.getInt("distance"));
                                stop.setID(stopdetails.getString("stop_id"));
                                Log.d("TAG",stop.getName());

                                JSONArray jsonArray2=stopdetails.getJSONArray("stop_points");
                                JSONObject stoploc = jsonArray2.getJSONObject(0);
                                LocCord loc = new LocCord(stoploc.getDouble("stop_lon"),stoploc.getDouble("stop_lat"));
                                stop.setLocation(loc);
                                busStops.add(stop);
                            }
                            nearbyListAdapter.notifyDataSetChanged();
                            if(pd!=null &&pd.isShowing()){
                                pd.dismiss();
                                displayNearbyList();
                            }
                        }
                        catch(Exception e) {
                            Log.d("NETWORK","JSON RETRIEVAL FAILED");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", error.getMessage() == null ? "no message" : error.getMessage() );
            }
        });
        networkqueue.add(request);
    }

    public  void displayNearbyList(){


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            if(currLocation.getLon()==0 && currLocation.getLat()==0){
                pd = new ProgressDialog(context);
                pd.setTitle("Getting Location");
                pd.setMessage("Please Wait");
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }
            else {
                builder.setTitle("Choose Stop");
                builder.setAdapter(nearbyListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusStopDetails clickedStop = busStops.get(which);
                        //intent to BusListActivity for normal list
                        Intent intent = new Intent(context, BusListTabsActivity.class);
                        intent.putExtra("BusStopID", clickedStop.getID());
                        intent.putExtra("BusStopName", clickedStop.getName());
                        intent.putExtra("BusStopLat",clickedStop.getLocation().getLat());
                        intent.putExtra("BusStopLon",clickedStop.getLocation().getLon());
                        intent.putExtra("CurrLat",currLocation.getLat());
                        intent.putExtra("CurrLon",currLocation.getLon());
                        startActivity(intent);
                    }
                });
                builder.show();
            }
    }


    //location services
    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //show alert to enable location services
    public void showLocSetAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Enable Location");
        dialog.setMessage("Your Location Settings are turned 'Off'. \nPlease Enable location for the best experience.");
        dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public void getCurrLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(true);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            Log.d("TAG", "Requesting loc updates");
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 5 * 1000, 10, locationListener);
        } else {
            Log.d("TAG", "Can't get provider");
        }
    }



}
