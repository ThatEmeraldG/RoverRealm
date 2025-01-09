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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp_finalproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private LinearLayout logoutBtn, editProfileBtn;
    private TextView tvUsername, tvUserEmail, tvUserId;
    private FirebaseAuth auth;
    private FirebaseUser authUser;
    private FirebaseFirestore db;
    private User user;
//    private UserViewModel userViewModel;
    private SwitchCompat nightModeSwitch;
    private boolean nightMode;
    private SharedPreferences modePreferences;
    private SharedPreferences.Editor modeEditor;


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
        modePreferences = requireActivity().getSharedPreferences("modePreferences", Context.MODE_PRIVATE);
        nightMode = modePreferences.getBoolean("night", false);
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

        auth = FirebaseAuth.getInstance();
        authUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
//        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        nightModeSwitch = view.findViewById(R.id.nightModeSwitch);
        handleNightMode();

        fetchUser();
//        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                tvUsername.setText(user.getUsername());
//                tvUserEmail.setText(user.getEmail());
//                tvUserId.setText(user.getUserId());
//            }
//        });

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
            }
        });

        return view;
    }

    private void handleNightMode() {
        if (nightMode) {
            nightModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        nightModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightModeSwitch.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    nightMode = true;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    nightMode = false;
                }
                modeEditor = modePreferences.edit();
                modeEditor.putBoolean("night", nightMode);
                modeEditor.apply();
            }
        });
    }

    private void fetchUser() {
        if (authUser == null) {
            Toast.makeText(requireContext(), "You are not authenticated. Please Login!", Toast.LENGTH_SHORT).show();
            Log.e("AuthError", "User is not authenticated");
            return;
        }

        db.collection("Users")
                .document(authUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User u = document.toObject(User.class);
                                if (u != null) {
                                    user = u;
                                    Log.d("FirestoreSuccess", "User fetched successfully");

                                    // Update TextView with user data
                                    tvUsername.setText(user.getUsername());
                                    tvUserEmail.setText(user.getEmail());
                                    tvUserId.setText(user.getUserId());
                                } else {
                                    Log.e("FirestoreError", "Failed to map document to User class");
                                }
                            } else {
                                Log.e("FirestoreError", "No User document found");
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching user", task.getException());
                        }
                    }
                });
    }

}