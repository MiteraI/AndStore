package com.example.andstore.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andstore.R;
import com.example.andstore.activities.LoginActivity;
import com.example.andstore.activities.OrderActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    LinearLayout logoutButton;
    TextView usernameText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment_layout, container, false);

        // Change username text
        usernameText = view.findViewById(R.id.username_text);
        usernameText.setText(mAuth.getCurrentUser().getEmail());

        // Logout button
        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return view;
    }
}