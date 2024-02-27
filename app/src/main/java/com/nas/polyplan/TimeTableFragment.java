package com.nas.polyplan;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.TimeTableFragmentBinding;

import java.util.ArrayList;

public class TimeTableFragment extends Fragment {

    private TimeTableFragmentBinding binding;
    private TimeTableAdapter timeTableAdapter;
    private SharedPreferences familyId;
    private String d;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = TimeTableFragmentBinding.inflate(inflater, container, false);

        familyId = getActivity().getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        getData();

        View root = binding.getRoot();
        return root;
    }

    private void getData() {


        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {

            ArrayList<User> users = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                users.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    User user = ds.getValue(User.class);

                    if (user.getFamily().equals(d)) {

                        users.add(user);

                    }

                }

                binding.rv.setHasFixedSize(false);

                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                timeTableAdapter = new TimeTableAdapter(getContext(), users);
                binding.rv.setAdapter(timeTableAdapter);
                timeTableAdapter.notifyDataSetChanged();

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