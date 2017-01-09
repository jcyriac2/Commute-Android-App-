package com.example.james.commute;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by james on 12/19/2016.
 */

public class BusETAListAdapter extends BaseAdapter {
    private List<BusDetails> BusList;
    Context context;
    private LayoutInflater inflater;

    public BusETAListAdapter(Context context, List<BusDetails> buslist){
        this.context=context;
        this.BusList=buslist;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return BusList.size();
    }

    @Override
    public Object getItem(int position) {
        return BusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.bus_timing_list,null);
        }
        String Busname = BusList.get(position).getNumber();
        int ETA = BusList.get(position).getETA();

        TextView BusNumText = (TextView)convertView.findViewById(R.id.BusName);
        TextView ETAtext = (TextView)convertView.findViewById(R.id.ArrivalTime);

        BusNumText.setText(Busname);
        convertView.setBackgroundColor(Color.parseColor("#"+BusList.get(position).getColor())+10);
        ETAtext.setText(ETA+" min");

        return convertView;
    }
}
