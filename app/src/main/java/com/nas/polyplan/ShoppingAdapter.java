package com.nas.polyplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {

    private ArrayList<Item> title = new ArrayList<>();
    private Context context;
    private static ClickListener clickListener;

    public ShoppingAdapter(Context context, ArrayList<Item> title) {
        this.title = title;
        this.context = context;
    }


    @NonNull
    @Override
    public ShoppingAdapter.ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_row, parent, false);

        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {

        holder.title.setText(title.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class ShoppingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private TextView title;

        public ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ShoppingAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
