package com.nas.polyplan;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.DocumentsFragmentBinding;

import java.util.ArrayList;

public class DocumentsFragment extends Fragment {

    private DocumentsFragmentBinding binding;
    private DocumentAdapter documentAdapter;
    private SharedPreferences familyId;
    private String d;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DocumentsFragmentBinding.inflate(inflater, container, false);

        familyId = getActivity().getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        getData();

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), UploadDocument.class));

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

    private void getData() {


        FirebaseDatabase.getInstance().getReference().child("document").addValueEventListener(new ValueEventListener() {

            ArrayList<Document> documents = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                documents.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Document document = ds.getValue(Document.class);

                    if (document.getFamily().equals(d)) {

                        documents.add(document);

                    }

                }

                binding.rv.setHasFixedSize(false);

                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                documentAdapter = new DocumentAdapter(getContext(), documents);
                binding.rv.setAdapter(documentAdapter);
                documentAdapter.notifyDataSetChanged();

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