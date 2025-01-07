package com.example.mp_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private LinearLayout logoutBtn;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireContext();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = view.findViewById(R.id.btn_logout);
        nightModeSwitch = view.findViewById(R.id.nightModeSwitch);
        modePreferences = requireActivity().getSharedPreferences("modePreferences", Context.MODE_PRIVATE);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(context, "Signed Out Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        handleNightMode();

        return view;
    }

    private void handleNightMode() {
        if(nightMode){
            nightModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        nightModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    modeEditor = modePreferences.edit();
                    modeEditor.putBoolean("night", false);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    modeEditor = modePreferences.edit();
                    modeEditor.putBoolean("night", true);
                }
                modeEditor.apply();
            }
        });
    }
}