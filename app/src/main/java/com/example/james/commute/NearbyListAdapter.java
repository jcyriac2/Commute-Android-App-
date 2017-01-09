package com.example.james.commute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by james on 12/2/2016.
 */

public class NearbyListAdapter extends BaseAdapter {
    private List<BusStopDetails> busStops;
    Context context;
    private LayoutInflater inflator;

    public NearbyListAdapter(Context context, List<BusStopDetails> stops){
        this.context = context;
        this.busStops = stops;
        this.inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return busStops.size();
    }

    @Override
    public Object getItem(int position) {
        return busStops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflator.inflate(R.layout.stop_nearby_row,null);
        }

        String name = busStops.get(position).getName();
        int dist = busStops.get(position).getDistance();

        TextView stoptext = (TextView)convertView.findViewById(R.id.busStop);
        TextView disttext = (TextView)convertView.findViewById(R.id.distance);

        stoptext.setText(name);
        disttext.setText(dist+" ft");

        return convertView;
    }
}
