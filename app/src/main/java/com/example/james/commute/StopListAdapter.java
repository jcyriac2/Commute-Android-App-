package com.example.james.commute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

/**
 * Created by james on 11/22/2016.
 */

public class StopListAdapter extends BaseAdapter {

    private List stops;
    Context context;
    private LayoutInflater inflater;

    public StopListAdapter(Context context, List stops){
        this.stops=stops;
        this.context=context;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return stops.size();
    }

    @Override
    public Object getItem(int position) {
        return stops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.stop_list_row,null);
        }

        String stop = (String) stops.get(position);

        TextView stopview = (TextView)convertView.findViewById(R.id.stop_text);
        stopview.setText(stop);
        return convertView;
    }
}
