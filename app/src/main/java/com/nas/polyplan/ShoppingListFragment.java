package com.nas.polyplan;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.ShoppingListFragmentBinding;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment {

    private ShoppingListFragmentBinding binding;
    private ShoppingAdapter shoppingAdapter;
    private SharedPreferences familyId;
    private String d;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = ShoppingListFragmentBinding.inflate(inflater, container, false);

        familyId = getActivity().getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        getData();

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddItemDialog(view);

            }
        });

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();
                binding.swipe.setRefreshing(false);

            }
        });

        View root = binding.getRoot();
        return root;
    }

    private void showAddItemDialog(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.add_shopping_item, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.add);

        final EditText title = dialogView.findViewById(R.id.title);
        final Button close = dialogView.findViewById(R.id.close);
        final Button add = dialogView.findViewById(R.id.add);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(title.getText().toString().trim())) {

                    FirebaseDatabase.getInstance().getReference("shopping").push().setValue(new Item(title.getText().toString().trim(), d));
                    b.dismiss();
                } else {

                    title.setError("Enter item title");

                }

            }
        });

    }

    private void showDeleteItemDialog(Item item, View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.delete_item_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Delete Item");

        final Button yes = dialogView.findViewById(R.id.yes);
        final Button no = dialogView.findViewById(R.id.no);
        final TextView title = dialogView.findViewById(R.id.title);

        title.setText("Do you really want to delete " + item.getTitle() + " from the list?");

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseDatabase.getInstance().getReference().child("shopping").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {


                            if (ds.getValue(Item.class).getTitle().equals(item.getTitle())) {

                                FirebaseDatabase.getInstance().getReference("shopping").child(ds.getKey()).removeValue();

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                b.dismiss();

            }
        });


    }

    private void getData() {


        FirebaseDatabase.getInstance().getReference().child("shopping").addValueEventListener(new ValueEventListener() {

            ArrayList<Item> items = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                items.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Item item = ds.getValue(Item.class);

                    if (item.getFamily().equals(d)) {

                        items.add(item);

                    }

                }

                binding.rv.setHasFixedSize(false);

                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                shoppingAdapter = new ShoppingAdapter(getContext(), items);
                shoppingAdapter.setOnItemClickListener(new ShoppingAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        showDeleteItemDialog(items.get(position), v);

                    }
                });
                binding.rv.setAdapter(shoppingAdapter);
                shoppingAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}