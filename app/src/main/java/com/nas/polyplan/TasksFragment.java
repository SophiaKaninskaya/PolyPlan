package com.nas.polyplan;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.nas.polyplan.databinding.TasksFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private TasksFragmentBinding binding;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> t = new ArrayList<>();
    private SharedPreferences familyId;
    private String d;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = TasksFragmentBinding.inflate(inflater, container, false);

        familyId = getActivity().getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        getData();

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddTaskDialog(view);

            }
        });

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData();
                binding.swipe.setRefreshing(false);

            }
        });

        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filter(binding.searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        View root = binding.getRoot();
        return root;
    }

    private void filter(String text) {
        text.toLowerCase();
        List<Task> temp = new ArrayList();
        for (Task task : t) {
            if (task.getText().toLowerCase().contains(text) || task.getResponsible().toLowerCase().equals(text) || task.getStatus().toLowerCase().contains(text)) {
                temp.add(task);
            }
        }

        taskAdapter.updateList(temp);
    }

    private void showAddTaskDialog(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.add_task_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.add_task);

        final EditText text = dialogView.findViewById(R.id.text);
        final EditText responsible = dialogView.findViewById(R.id.responsible);
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

                if (!TextUtils.isEmpty(text.getText().toString().trim()) && !TextUtils.isEmpty(responsible.getText().toString().trim())) {

                    FirebaseDatabase.getInstance().getReference("task").push().setValue(new Task(d, "incomplete", responsible.getText().toString().trim(), text.getText().toString().trim()));
                    b.dismiss();
                } else {

                    text.setError("Enter data!");

                }

            }
        });

    }

    private void getData() {

        ArrayList<Task> tasks = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("task").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tasks.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Task task = ds.getValue(Task.class);

                    if (task.getFamily().equals(d)) {

                        tasks.add(task);

                    }

                }

                binding.rv.setHasFixedSize(false);
                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                taskAdapter = new TaskAdapter(getContext(), tasks);
                taskAdapter.setOnItemClickListener(new TaskAdapter.TaskClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        showMarkAsCompleteDialog(tasks.get(position), v);

                    }
                });
                binding.rv.setAdapter(taskAdapter);
                taskAdapter.notifyDataSetChanged();

                t = tasks;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showMarkAsCompleteDialog(Task task, View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.mark_as_complete_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.mark_as_complete);

        final Button yes = dialogView.findViewById(R.id.yes);
        final Button no = dialogView.findViewById(R.id.no);
        final TextView title = dialogView.findViewById(R.id.title);

        title.setText(task.getText().toString() + "\n Have you completed this task?");

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

                FirebaseDatabase.getInstance().getReference().child("task").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {


                            if (ds.getValue(Task.class).getText().equals(task.getText())) {

                                FirebaseDatabase.getInstance().getReference("task").child(ds.getKey()).child("status").setValue("complete");

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}