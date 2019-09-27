package com.hamidelmamoun.to_do.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.hamidelmamoun.to_do.R;
import com.hamidelmamoun.to_do.model.Priority;

import java.util.ArrayList;

import static android.view.View.inflate;

public class PriorityAdapter extends BaseAdapter implements SpinnerAdapter {

    Context context;
    ArrayList<Priority> priorities = new ArrayList<>();

    public PriorityAdapter(Context context, ArrayList<Priority> priorities) {
        this.priorities = priorities;
        this.context = context;
    }

    @Override
    public int getCount() {
        return priorities.size();
    }

    @Override
    public Object getItem(int position) {
        return priorities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  inflate(context, R.layout.item_priority, null);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Priority priority = priorities.get(position);
        View view;
        view =  inflate(context, R.layout.item_priority, null);
        final ImageView imageView = view.findViewById(R.id.flag_iv);
        final TextView textView = view.findViewById(R.id.priority_tv);

        imageView.setImageDrawable(priority.getmDrawable());
        textView.setText(priority.getmName());

        return view;
    }
}