package com.example.mp_finalproject;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mp_finalproject.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatedPostsViewModel extends ViewModel {
    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public void fetchCreatedPosts() {
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            posts.setValue(new ArrayList<>());
            return;
        }

        db.collection("Posts")
                .whereEqualTo("authorId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        posts.setValue(null);
                        Log.e("FirestoreError", "Error fetching posts", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        posts.setValue(Collections.emptyList());
                        Log.e("FirestoreError", "QuerySnapshot is null or empty");
                        return;
                    }

                    List<Post> postList = value.toObjects(Post.class);
                    posts.setValue(postList);
                });
    }

}
