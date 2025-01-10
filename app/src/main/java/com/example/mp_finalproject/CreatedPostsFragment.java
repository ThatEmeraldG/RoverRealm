package com.example.mp_finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mp_finalproject.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatedPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatedPostsFragment extends Fragment {

    private CreatedPostsViewModel viewModel;
    private RecyclerView rv_createdPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private TextView tvNotice;

    public CreatedPostsFragment() {
        // Required empty public constructor
    }

//    public static CreatedPostsFragment newInstance(String param1, String param2) {
//        CreatedPostsFragment fragment = new CreatedPostsFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreatedPostsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = requireContext();
        View view = inflater.inflate(R.layout.fragment_created_posts, container, false);

        rv_createdPosts = view.findViewById(R.id.rv_createdPosts);
        rv_createdPosts.setLayoutManager(new LinearLayoutManager(context));

        tvNotice = view.findViewById(R.id.tv_notice);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(context, postList);
        rv_createdPosts.setAdapter(postAdapter);

        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts.isEmpty()) {
                tvNotice.setVisibility(View.VISIBLE);
                tvNotice.setText("You haven't posted anything~");
            } else {
                tvNotice.setVisibility(View.GONE);
            }
            postAdapter.setPosts(posts);
            postAdapter.notifyDataSetChanged();
        });

        viewModel.fetchCreatedPosts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchCreatedPosts();
    }

}