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

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private ArrayList<Document> title = new ArrayList<>();
    private Context context;

    public DocumentAdapter(Context context, ArrayList<Document> title) {
        this.title = title;
        this.context = context;
    }


    @NonNull
    @Override
    public DocumentAdapter.DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_row, parent, false);

        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {

        holder.title.setText(title.get(position).getTitle());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Intent i = new Intent(context.getApplicationContext(), ShowDocumentActivity.class);
                i.putExtra("DocumentTitle", title.get(position).getTitle());
                i.putExtra("family", title.get(position).getFamily());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener itemClickListener;


        private TextView title;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
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
