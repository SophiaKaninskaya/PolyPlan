package com.nas.polyplan;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.ActivityCreateFamilyBinding;

import java.util.HashMap;
import java.util.Map;

public class CreateFamily extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityCreateFamilyBinding binding;
    private SharedPreferences inFamily, familyId;
    String s, d, role = "parent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateFamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inFamily = getSharedPreferences("inFamily", MODE_PRIVATE);
        s = inFamily.getString("inFamily", "0");
        familyId = getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");


        if (s.equals("1")) {

            startActivity(new Intent(CreateFamily.this, NavigationActivity.class));

        } else {

            binding.create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showCreateFamilyDialog();

                }
            });


            binding.join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showJoinFamilyDialog();

                }
            });

        }


    }

    private void showCreateFamilyDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.create_family_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.create_family);

        final EditText name = dialogView.findViewById(R.id.name);
        final Button close = dialogView.findViewById(R.id.close);
        final Button create = dialogView.findViewById(R.id.create);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(name.getText().toString().trim())) {
                    Map<String, String> members = new HashMap<>();
                    members.put(user.getUid(), "parent");
                    FirebaseDatabase.getInstance().getReference("family").push().setValue(new Family(name.getText().toString().trim(), members, 0));
                    inFamily.edit().putString("inFamily", "1").apply();
                    FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("role").setValue("parent");
                    FirebaseDatabase.getInstance().getReference("family").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds : snapshot.getChildren()) {

                                if (ds.getValue(Family.class).getName().equals(name.getText().toString().trim())) {

                                    FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("family").setValue(ds.getKey());
                                    familyId.edit().putString("familyId", ds.getKey()).apply();

                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    b.dismiss();
                    startActivity(new Intent(CreateFamily.this, NavigationActivity.class));
                    finish();

                } else {

                    name.setError("Enter family name!");

                }


            }
        });

    }


    private void showJoinFamilyDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.join_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Joint family");

        final EditText id = dialogView.findViewById(R.id.id);
        final Button close = dialogView.findViewById(R.id.close);
        final Button join = dialogView.findViewById(R.id.join);
        final Spinner role_spinner = dialogView.findViewById(R.id.role_spinner);

        String[] roles = {"parent", "child"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, roles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(arrayAdapter);

        final AlertDialog b = dialogBuilder.create();
        b.show();


        role_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                role = roles[i];
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                b.dismiss();

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("family").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            if (ds.getKey().equals(id.getText().toString().trim())) {

                                FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("family").setValue(ds.getKey());
                                familyId.edit().putString("familyId", ds.getKey()).apply();
                                FirebaseDatabase.getInstance().getReference("family").child(ds.getKey()).child("members").child(user.getUid()).setValue(role);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("inFamily").setValue(1);
                inFamily.edit().putString("inFamily", "1").apply();
                FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("role").setValue(role);
                b.dismiss();
                startActivity(new Intent(CreateFamily.this, NavigationActivity.class));
                finish();


            }
        });


    }

}