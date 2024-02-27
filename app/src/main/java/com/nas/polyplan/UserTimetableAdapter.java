package com.nas.polyplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserTimetableAdapter extends RecyclerView.Adapter<UserTimetableAdapter.UserTimetableViewHolder> {

    private ArrayList<Timetable> times = new ArrayList<>();
    private Context context;
    private static TimeClickListener timeClickListener;

    public UserTimetableAdapter(Context context, ArrayList<Timetable> times) {
        this.times = times;
        //this.context = context;
    }


    @NonNull
    @Override
    public UserTimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_timetable_row, parent, false);

        return new UserTimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTimetableViewHolder holder, int position) {

        holder.day_of_the_week.setText(times.get(position).getDay_of_the_week());
        holder.timetable_text.setText(times.get(position).getText());

    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public static class UserTimetableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView day_of_the_week, timetable_text;

        public UserTimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            day_of_the_week = itemView.findViewById(R.id.day_of_the_week);
            timetable_text = itemView.findViewById(R.id.timetable_text);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            timeClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(TimeClickListener clickListener) {
        timeClickListener = clickListener;
    }

    public interface TimeClickListener {
        void onItemClick(int position, View v);
    }
}

