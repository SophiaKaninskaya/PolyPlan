package com.nas.polyplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nas.polyplan.databinding.ActivityUploadDocumentBinding;

public class UploadDocument extends AppCompatActivity {

    private ActivityUploadDocumentBinding binding;
    private ActivityResultLauncher<String> getDocument;
    private SharedPreferences familyId;
    private String d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        familyId = getSharedPreferences("familyId", MODE_PRIVATE);
        d = familyId.getString("familyId", "");

        getDocument = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                StorageReference reference = FirebaseStorage.getInstance().getReference("documents").child(d).child(binding.title.getText().toString() + ".pdf");
                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseDatabase.getInstance().getReference("document").push().setValue(new Document(binding.title.getText().toString(), d));
                        startActivity(new Intent(UploadDocument.this, NavigationActivity.class));
                        finish();

                    }
                });

            }
        });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectPDF();

            }
        });

    }

    private void selectPDF() {

        getDocument.launch("application/pdf");


    }

}