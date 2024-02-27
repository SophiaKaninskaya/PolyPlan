package com.nas.polyplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks = new ArrayList<>();
    private Context context;
    private static TaskClickListener taskClickListener;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.tasks = tasks;
        this.context = context;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        holder.status.setText("status: " + tasks.get(position).getStatus());
        holder.text.setText(tasks.get(position).getText());
        holder.responsible.setText("responsible: " + tasks.get(position).getResponsible());

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView status, text, responsible;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.status);
            text = itemView.findViewById(R.id.text);
            responsible = itemView.findViewById(R.id.responsible);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            taskClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(TaskClickListener clickListener) {
        taskClickListener = clickListener;
    }

    public interface TaskClickListener {
        void onItemClick(int position, View v);
    }

    public void updateList(List<Task> list) {
        tasks = (ArrayList<Task>) list;
        notifyDataSetChanged();
    }

}
