package com.nas.polyplan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private ArrayList<Expense> expenses = new ArrayList<>();
    private Context context;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        this.expenses = expenses;
        this.context = context;
    }


    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_row, parent, false);

        return new ExpenseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {

        holder.amount.setText(Integer.valueOf(expenses.get(position).getAmount()).toString() + " ₽");
        holder.category.setText(expenses.get(position).getCategory());
        holder.date.setText(expenses.get(position).getDate());
        holder.type.setText(expenses.get(position).getType());

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private TextView amount, category, date, type;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.amount);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            type = itemView.findViewById(R.id.type);

        }

    }

    public void updateList(ArrayList<Expense> list) {
        expenses = list;
        notifyDataSetChanged();
    }

}
