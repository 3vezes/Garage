package com.ericrgon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ericrgon.model.LogEntry;

import static com.ericrgon.GarageActivity.CLOSED;
import static com.ericrgon.GarageActivity.OPEN;

/**
 * Created by ericrgon on 6/6/13.
 */
public class ActivityLogAdapter extends ArrayAdapter<LogEntry>{

    private final LayoutInflater mLayoutInflater;

    public ActivityLogAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LogEntry entry = getItem(position);
        View item = mLayoutInflater.inflate(R.layout.list_item,parent,false);

        TextView name = (TextView) item.findViewById(R.id.name);
        name.setText(entry.getName());

        TextView time = (TextView) item.findViewById(R.id.time);
        time.setText(entry.getTime());

        TextView state = (TextView) item.findViewById(R.id.status);
        state.setText(OPEN.equals(entry.getState()) ? "Opened" : "Closed");

        //Check the state to determine the item color.
        if(CLOSED.equals(entry.getState())){
            state.setBackgroundResource(R.color.purple);
        }
        else {
            state.setBackgroundResource(R.color.blue);
        }

        return item;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
