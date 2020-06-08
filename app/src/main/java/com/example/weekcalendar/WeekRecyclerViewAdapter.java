package com.example.weekcalendar;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WeekRecyclerViewAdapter extends RecyclerView.Adapter<WeekRecyclerViewAdapter.MyViewHolder> {

    private List<CustomDay> listOfDates;
    private MyOnDateClickListener mDateClickListener;
    private Activity a;
    private View eachDayView;
    private DatabaseHelper myDB;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private Button date;
        private Button month;
        private RecyclerView dayEvents;

        private LinearLayout.LayoutParams timeLayoutFormat;
        private LinearLayout.LayoutParams lastTimeLayoutFormat;
        private LinearLayout.LayoutParams eventLayoutFormat;

        private MyViewHolder(View itemView) {

            super(itemView);
            date = itemView.findViewById(R.id.date_button);
            month = itemView.findViewById(R.id.month_button);
            dayEvents = itemView.findViewById(R.id.all_events_layout);

//            timeLayoutFormat = new LinearLayout.LayoutParams
//                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//            timeLayoutFormat.setMargins(10, 0, 0, 50);
//
//            lastTimeLayoutFormat = new LinearLayout.LayoutParams
//                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//            lastTimeLayoutFormat.setMargins(10, 0, 0, 0);
//
//            eventLayoutFormat = new LinearLayout.LayoutParams
//                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 100f);
//            eventLayoutFormat.setMargins(5, 0, 0, 0);

//            eventLayout = eachDayView.findViewById(R.id.all_events_layout);
        }
    }

    public WeekRecyclerViewAdapter(List<CustomDay> list, MyOnDateClickListener dateClicker, Activity a, Context context) {
        this.listOfDates = list;
        this.mDateClickListener = dateClicker;
        this.a = a;
        this.myDB = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public WeekRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        eachDayView = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_day, parent, false);
        WeekRecyclerViewAdapter.MyViewHolder holder = new MyViewHolder(eachDayView);

        holder.date.setOnClickListener(v -> {
            String day = holder.date.getText().toString();
            String month = holder.month.getText().toString();
            mDateClickListener.onDateClickListener(day + " " + month);
        });

        holder.month.setOnClickListener(v -> {
            String day = holder.date.getText().toString();
            String month = holder.month.getText().toString();
            mDateClickListener.onDateClickListener(day + " " + month);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final WeekRecyclerViewAdapter.MyViewHolder holder, int position) {
        final CustomDay d = listOfDates.get(position);
        holder.date.setText(d.getdd().length() == 1 ? "0" + d.getdd() : d.getdd());
        holder.month.setText(d.getMMM());
        String day = d.getdd();
        if (day.length() == 1) {
            day = "0" + day;
        }
        // todo can we prepare this on create?
        String daySQL = d.getyyyy() + "-" + myDB.convertDate(d.getMMM()) + "-" + day;
        Cursor result = myDB.getEventData(daySQL);
        List<CustomEvent> temp = new ArrayList<>();
        for (int i = 0; i < result.getCount(); i++) {
            result.moveToNext();
            String id = result.getString(0);
            String title = result.getString(5);
            String startTime = result.getString(3);
            temp.add(new CustomEvent(id, title, startTime, ""));
        }

        EventRecyclerViewAdapter e = new EventRecyclerViewAdapter(temp, (MyOnEventClickListener) a); // can store in holder?
        LinearLayoutManager LLM = new LinearLayoutManager(a); // can store in holder?
        holder.dayEvents.setLayoutManager(LLM);
        holder.dayEvents.setAdapter(e);
    }

    @Override
    public int getItemCount() {
        return this.listOfDates == null ? 0 : this.listOfDates.size();
    }
}