package com.nas.polyplan;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TasksViewModel extends ViewModel {
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    List<Task> t = new ArrayList<>();

    public TasksViewModel() {

        FirebaseDatabase.getInstance().getReference().child("task").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Task task = ds.getValue(Task.class);

                    t.add(task);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tasks.setValue(t);

    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }
}