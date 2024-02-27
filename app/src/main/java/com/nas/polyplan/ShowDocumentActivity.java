package com.nas.polyplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.nas.polyplan.databinding.ActivityShowDocumentBinding;

import java.io.File;
import java.io.IOException;

public class ShowDocumentActivity extends AppCompatActivity {

    private ActivityShowDocumentBinding binding;
    private String selected, family;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference pathReference;
    private PDFView pdfView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        selected = i.getStringExtra("DocumentTitle");
        family = i.getStringExtra("family");

        pathReference = storageRef.child("documents/" + family + "/" + selected + ".pdf");

        pdfView = findViewById(R.id.pdfView);

        final File localFile;
        try {
            localFile = File.createTempFile(selected, ".pdf");

            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    pdfView.fromFile(localFile)
                            .pages(0, 2, 1, 3, 3, 3)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)

                            .enableAnnotationRendering(false)
                            .password(null)
                            .scrollHandle(null)
                            .enableAntialiasing(true)
                            .spacing(0)
                            .load();

                    progressBar.setVisibility(View.GONE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}