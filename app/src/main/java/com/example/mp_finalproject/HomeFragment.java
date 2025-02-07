package com.example.mp_finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mp_finalproject.adapter.PostAdapter;
import com.example.mp_finalproject.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView rv_posts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = requireContext();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rv_posts = view.findViewById(R.id.rv_posts);
        rv_posts.setLayoutManager(new LinearLayoutManager(context));

        postList = new ArrayList<>();

        postAdapter = new PostAdapter(context, postList);
        rv_posts.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        db.collection("Posts")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching posts", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.e("FirestoreError", "QuerySnapshot is null or empty");
                        return;
                    }

                    postList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Post post = snapshot.toObject(Post.class);
                        if (post != null) {
                            postList.add(post);
                        } else {
                            Log.e("FirestoreError", "Failed to map document to Post class");
                        }
                    }

                    postAdapter.notifyDataSetChanged();
                });
    }
}