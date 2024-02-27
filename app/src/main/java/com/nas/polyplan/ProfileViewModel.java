package com.nas.polyplan;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userData = new MutableLiveData<>(null);
    private final MutableLiveData<Family> familyData = new MutableLiveData<>(null);

    public ProfileViewModel() {
        FirebaseDatabase.getInstance()
                .getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userData.setValue(snapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    public LiveData<User> getUser() {
        return userData;
    }

    public LiveData<Family> getFamily() {
        return familyData;
    }
}