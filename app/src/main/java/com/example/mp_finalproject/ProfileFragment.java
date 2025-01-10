package com.example.mp_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp_finalproject.model.ProfileAdapter;
import com.example.mp_finalproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private LinearLayout logoutBtn, editProfileBtn;
    private TextView tvUsername, tvUserEmail, tvUserId;
    private UserViewModel userViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ProfileAdapter adapter;
    private SwitchCompat nightModeSwitch;


    public ProfileFragment() {
        // Required empty public constructor
    }

//    public static ProfileFragment newInstance(String param1, String param2) {
//        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireContext();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvUserEmail = view.findViewById(R.id.tv_userEmail);
        tvUserId = view.findViewById(R.id.tv_userId);
        logoutBtn = view.findViewById(R.id.btn_logout);
        editProfileBtn = view.findViewById(R.id.btn_editProfile);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        nightModeSwitch = view.findViewById(R.id.nightModeSwitch);
        handleNightMode();

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvUsername.setText(user.getUsername());
                tvUserEmail.setText(user.getEmail());
                tvUserId.setText(user.getUserId());
            } else {
                tvUsername.setText("Not Logged In");
                tvUserEmail.setText("");
                tvUserId.setText("");
            }
        });

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                    if(user != null){
                        Intent intent = new Intent(context, EditProfile.class);
                        String username = user.getUsername();
                        String email = user.getEmail();
                        String password = user.getPassword();
                        String userId = user.getUserId();

                        intent.putExtra("username", username);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("user_id", userId);

                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "User Data not available", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new CreatedPostsFragment());
        fragmentList.add(new LikedPostsFragment());

        adapter = new ProfileAdapter(getChildFragmentManager(), getLifecycle(), fragmentList);
        viewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Created Posts");
                            break;
                        case 1:
                            tab.setText("Liked Posts");
                            break;
                    }
                });
        tabLayoutMediator.attach();

        userViewModel.fetchUser();

        return view;
    }

    private void handleNightMode() {
        boolean nightMode = ((MainActivity) requireActivity()).getNightMode();
        nightModeSwitch.setChecked(nightMode);
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ((MainActivity) requireActivity()).setNightMode(isChecked);
        });
    }

}