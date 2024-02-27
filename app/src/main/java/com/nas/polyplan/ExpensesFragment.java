package com.nas.polyplan;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nas.polyplan.databinding.ExpensesFragmentBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpensesFragment extends Fragment {

    private ExpensesFragmentBinding binding;
    private ExpenseAdapter expenseAdapter;
    private int balance;
    private SharedPreferences familyId;
    private String d;
    private String date = "";
    private String type = "Income";
    private ArrayList<Expense> e = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = ExpensesFragmentBinding.inflate(inflater, container, false);

        familyId = getActivity().getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        FirebaseDatabase.getInstance().getReference("family").child(d).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                balance = snapshot.getValue(Integer.class);
                binding.efCurrentBalanceMsg.setText(Integer.valueOf(balance).toString() + " ₽");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getData();

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddExpenseDialog(view);

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
        ArrayList<Expense> temp = new ArrayList();
        for (Expense expense : e) {
            if (expense.getCategory().toLowerCase().contains(text) || expense.getDate().toLowerCase().equals(text) || expense.getType().toLowerCase().contains(text) || Integer.valueOf(expense.getAmount()).toString().toLowerCase().equals(text)) {
                temp.add(expense);
            }
        }

        expenseAdapter.updateList(temp);
    }

    private void showAddExpenseDialog(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.add_expense_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.add_expense);

        final EditText amount = dialogView.findViewById(R.id.amount);
        final EditText category = dialogView.findViewById(R.id.category);
        final TextView choose_date = dialogView.findViewById(R.id.choose_date);
        final Spinner type_spinner = dialogView.findViewById(R.id.type_spinner);
        final Button close = dialogView.findViewById(R.id.close);
        final Button add = dialogView.findViewById(R.id.add);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();

        String[] types = {"Income", "Outcome"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(arrayAdapter);

        choose_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dataPickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1++;
                        String x = "";

                        if (i1 < 10) x = "0";

                        date = i2 + "." + i1 + "." + i;

                        choose_date.setText(i2 + "." + x + i1 + "." + i);
                    }
                }, mYear, mMonth, mDay);

                dataPickerDialog.show();

            }
        });


        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = types[i];
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

                if (!TextUtils.isEmpty(amount.getText().toString().trim()) && !TextUtils.isEmpty(category.getText().toString().trim()) && !TextUtils.isEmpty(date.toString().trim()) && !TextUtils.isEmpty(type.trim())) {

                    FirebaseDatabase.getInstance().getReference("expense").push().setValue(new Expense(d, Integer.parseInt(amount.getText().toString().trim()), category.getText().toString().trim(), date.toString().trim(), type.trim()));
                    if (type.equals("Income"))
                        FirebaseDatabase.getInstance().getReference("family/" + d).child("balance").setValue(balance + Integer.parseInt(amount.getText().toString().trim()));
                    else
                        FirebaseDatabase.getInstance().getReference("family/" + d).child("balance").setValue(balance - Integer.parseInt(amount.getText().toString().trim()));
                    b.dismiss();
                } else {

                    amount.setError("Enter data!");

                }

            }
        });

    }


    private void getData() {

        ArrayList<Expense> expenses = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("expense").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                expenses.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Expense expense = ds.getValue(Expense.class);

                    if (expense.getFamily().equals(d)) {

                        expenses.add(expense);

                    }

                }

                binding.rv.setHasFixedSize(false);
                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                expenseAdapter = new ExpenseAdapter(getContext(), expenses);
                binding.rv.setAdapter(expenseAdapter);
                expenseAdapter.notifyDataSetChanged();

                e = expenses;

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