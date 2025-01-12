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
import android.widget.TextView;

import com.example.mp_finalproject.adapter.PostAdapter;
import com.example.mp_finalproject.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikedPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedPostsFragment extends Fragment {

    private RecyclerView rv_likedPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private TextView tvNotice;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public LikedPostsFragment() {
        // Required empty public constructor
    }

//    public static LikedPostsFragment newInstance() {
//        LikedPostsFragment fragment = new LikedPostsFragment();
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
        View view = inflater.inflate(R.layout.fragment_liked_posts, container, false);

        rv_likedPosts = view.findViewById(R.id.rv_likedPosts);
        rv_likedPosts.setLayoutManager(new LinearLayoutManager(context));

        tvNotice = view.findViewById(R.id.tv_notice);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(context, postList);
        rv_likedPosts.setAdapter(postAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        fetchPosts(userId);

        return view;
    }

    private void fetchPosts(String userId) {
        if (userId == null) {
            tvNotice.setText("You are not authenticated, please login!");
            tvNotice.setVisibility(View.VISIBLE);
            return;
        }

        // Fetch the user's upvoted posts
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDocument -> {
                    if (userDocument.exists()) {
                        List<String> upvotedPostIds = (List<String>) userDocument.get("upvotedPostId");

                        if (upvotedPostIds != null && !upvotedPostIds.isEmpty()) {
                            fetchLikedPosts(upvotedPostIds);
                        } else {
                            tvNotice.setText("No liked posts found.");
                            tvNotice.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("FirestoreError", "User document does not exist");
                        tvNotice.setText("User data not found.");
                        tvNotice.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user document", e);
                    tvNotice.setText("Failed to fetch user data.");
                    tvNotice.setVisibility(View.VISIBLE);
                });
    }

    private void fetchLikedPosts(List<String> upvotedPostIds) {
        db.collection("Posts")
                .whereIn("postId", upvotedPostIds)
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