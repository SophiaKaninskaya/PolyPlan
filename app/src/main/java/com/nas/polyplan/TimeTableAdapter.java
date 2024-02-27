package com.nas.polyplan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

    private ArrayList<User> names = new ArrayList<>();
    private Context context;

    public TimeTableAdapter(Context context, ArrayList<User> names) {
        this.names = names;
        this.context = context;
    }


    @NonNull
    @Override
    public TimeTableAdapter.TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timetable_row, parent, false);

        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {

        holder.name.setText(names.get(position).getUsername());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Intent i = new Intent(context.getApplicationContext(), UserTimetableActivity.class);
                i.putExtra("id", names.get(position).getId());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class TimeTableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener itemClickListener;


        private TextView name;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);


        }


        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getPosition(), false);
        }
    }


    interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}
