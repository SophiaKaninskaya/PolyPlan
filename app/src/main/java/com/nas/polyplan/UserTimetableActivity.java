package com.nas.polyplan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.ActivityUserTimetableBinding;

import java.util.ArrayList;

public class UserTimetableActivity extends AppCompatActivity {

    private ActivityUserTimetableBinding binding;
    private UserTimetableAdapter userTimetableAdapter;
    private String selected, day = "Monday";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserTimetableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        selected = i.getStringExtra("id");

        if (!selected.equals(FirebaseAuth.getInstance().getUid())) {

            binding.add.setVisibility(View.GONE);

        }

        getData();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();
                binding.swipe.setRefreshing(false);

            }
        });

    }

    private void getData() {


        FirebaseDatabase.getInstance().getReference("timetable").addValueEventListener(new ValueEventListener() {

            ArrayList<Timetable> timetables = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                timetables.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Timetable timetable = ds.getValue(Timetable.class);

                    if (timetable.getUser().equals(selected)) {

                        timetables.add(timetable);

                    }

                }

                binding.rv.setHasFixedSize(false);

                binding.rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                userTimetableAdapter = new UserTimetableAdapter(getApplication(), timetables);
                userTimetableAdapter.setOnItemClickListener(new UserTimetableAdapter.TimeClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        showChangeTimetable(timetables.get(position));

                    }
                });
                binding.rv.setAdapter(userTimetableAdapter);
                userTimetableAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddTimetable();

            }
        });

    }

    private void showAddTimetable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.change_timetable_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.add_timetable);

        final EditText title = dialogView.findViewById(R.id.title);
        final Button close = dialogView.findViewById(R.id.close);
        final Button add = dialogView.findViewById(R.id.add);
        final Spinner day_spinner = dialogView.findViewById(R.id.day_spinner);

        add.setText("ADD");

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(arrayAdapter);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                day = days[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = title.getText().toString();

                if (!TextUtils.isEmpty(title.getText().toString().trim())) {

                    FirebaseDatabase.getInstance().getReference("timetable").push().setValue(new Timetable(FirebaseAuth.getInstance().getUid(), day, s));

                } else {

                    title.setError("Enter item title");

                }

                b.dismiss();

            }
        });
    }

    private void showChangeTimetable(Timetable timetable) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.change_timetable_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.change_timetable);

        final EditText title = dialogView.findViewById(R.id.title);
        final Button close = dialogView.findViewById(R.id.close);
        final Button add = dialogView.findViewById(R.id.add);
        final Spinner day_spinner = dialogView.findViewById(R.id.day_spinner);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, days);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day_spinner.setAdapter(arrayAdapter);

        title.setText(timetable.getText());

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        day_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                day = days[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = title.getText().toString();

                if (!TextUtils.isEmpty(title.getText().toString().trim())) {

                    FirebaseDatabase.getInstance().getReference().child("timetable").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds : snapshot.getChildren()) {


                                if (ds.getValue(Timetable.class).getDay_of_the_week().equals(timetable.getDay_of_the_week()) && ds.getValue(Timetable.class).getUser().equals(selected)) {

                                    FirebaseDatabase.getInstance().getReference("timetable").child(ds.getKey()).child("text").setValue(s);
                                    FirebaseDatabase.getInstance().getReference("timetable").child(ds.getKey()).child("day_of_the_week").setValue(day);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {

                    title.setError("Enter item title");

                }

                b.dismiss();

            }
        });

    }


}