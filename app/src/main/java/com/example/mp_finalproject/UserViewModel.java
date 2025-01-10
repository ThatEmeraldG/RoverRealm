package com.example.mp_finalproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_finalproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(); // For error handling
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    public LiveData<User> getUser() {
        return userLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchUser() {
        FirebaseUser authUser = auth.getCurrentUser();
        if (authUser == null) {
            errorMessage.setValue("User not authenticated.");
            userLiveData.setValue(null);
            return;
        }

        db.collection("Users")
                .document(authUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = document.toObject(User.class);
                            userLiveData.setValue(user);
                            errorMessage.setValue(null);
                        } else {
                            errorMessage.setValue("User document not found.");
                            userLiveData.setValue(null);
                        }
                    } else {
                        errorMessage.setValue("Error fetching user: " + task.getException().getMessage());
                        userLiveData.setValue(null);
                    }
                });
    }
}
